
#ifndef __XVIDENCODER_H__
#define __XVIDENCODER_H__
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <math.h>
#include <sys/time.h>
#include "xvid.h"


#undef READ_PNM

#define MAX_ZONES   64

#define DEFAULT_QUANT 400

typedef struct
{
	int frame;

	int type;
	int mode;
	int modifier;

	unsigned int greyscale;
	unsigned int chroma_opt;
	unsigned int bvop_threshold;
	unsigned int cartoon_mode;
} zone_t;

typedef struct
{
	int count;
	int size;
	int quants[32];
} frame_stats_t;

static const int motion_presets[] =
{
	/* quality 0 */
	0,

	/* quality 1 */
	XVID_ME_ADVANCEDDIAMOND16,

	/* quality 2 */
	XVID_ME_ADVANCEDDIAMOND16 | XVID_ME_HALFPELREFINE16,

	/* quality 3 */
	XVID_ME_ADVANCEDDIAMOND16 | XVID_ME_HALFPELREFINE16 | XVID_ME_ADVANCEDDIAMOND8 | XVID_ME_HALFPELREFINE8,

	/* quality 4 */
	XVID_ME_ADVANCEDDIAMOND16 | XVID_ME_HALFPELREFINE16 |
	XVID_ME_ADVANCEDDIAMOND8 | XVID_ME_HALFPELREFINE8 |
	XVID_ME_CHROMA_PVOP | XVID_ME_CHROMA_BVOP,

	/* quality 5 */
	XVID_ME_ADVANCEDDIAMOND16 | XVID_ME_HALFPELREFINE16 |
	XVID_ME_ADVANCEDDIAMOND8 | XVID_ME_HALFPELREFINE8 |
	XVID_ME_CHROMA_PVOP | XVID_ME_CHROMA_BVOP,

	/* quality 6 */
	XVID_ME_ADVANCEDDIAMOND16 | XVID_ME_HALFPELREFINE16 | XVID_ME_EXTSEARCH16 |
	XVID_ME_ADVANCEDDIAMOND8 | XVID_ME_HALFPELREFINE8 | XVID_ME_EXTSEARCH8 |
	XVID_ME_CHROMA_PVOP | XVID_ME_CHROMA_BVOP
};

#define ME_ELEMENTS (sizeof(motion_presets)/sizeof(motion_presets[0]))

static const int vop_presets[] =
{
	/* quality 0 */
	0,

	/* quality 1 */
	0,

	/* quality 2 */
	XVID_VOP_HALFPEL,

	/* quality 3 */
	XVID_VOP_HALFPEL | XVID_VOP_INTER4V,

	/* quality 4 */
	XVID_VOP_HALFPEL | XVID_VOP_INTER4V,

	/* quality 5 */
	XVID_VOP_HALFPEL | XVID_VOP_INTER4V |
	XVID_VOP_TRELLISQUANT,

	/* quality 6 */
	XVID_VOP_HALFPEL | XVID_VOP_INTER4V |
	XVID_VOP_TRELLISQUANT | XVID_VOP_HQACPRED,

};

#define VOP_ELEMENTS (sizeof(vop_presets)/sizeof(vop_presets[0]))

/* Maximum number of frames to encode */
#define ABS_MAXFRAMENR -1 /* no limit */

#ifndef READ_PNM
#define IMAGE_SIZE(x,y) ((x)*(y)*3/2)
#else
#define IMAGE_SIZE(x,y) ((x)*(y)*3)
#endif

#define SMALL_EPS (1e-10)

#define SWAP(a) ( (((a)&0x000000ff)<<24) | (((a)&0x0000ff00)<<8) | \
	(((a)&0x00ff0000)>>8)  | (((a)&0xff000000)>>24) )

typedef struct XvidParam
{
	int width;
	int height;
	int bitrate;
	int framerate;
	int gop;
	int colorspace;
	zone_t ZONES[MAX_ZONES];
	int NUM_ZONES;
	frame_stats_t framestats[7];
	int ARG_STATS;
	int ARG_SSIM;
	char* ARG_SSIM_PATH;
	int ARG_DUMP;
	int ARG_LUMIMASKING;
	int ARG_SINGLE;
	int ARG_QUALITY;
	int ARG_DWRATE;
	int ARG_DWSCALE;
	int ARG_MAXFRAMENR;
	int ARG_STARTFRAMENR;
	char *ARG_INPUTFILE;
	int ARG_INPUTTYPE;
	int ARG_SAVEMPEGSTREAM;
	char *ARG_OUTPUTFILE;
	char *ARG_TIMECODEFILE;
	int ARG_BQRATIO;
	int ARG_BQOFFSET;
	int ARG_MAXBFRAMES;
	int ARG_PACKED;
	int ARG_DEBUG;
	int ARG_VOPDEBUG;
	int ARG_TRELLIS;
	int ARG_QTYPE;
	int ARG_QMATRIX;
	int ARG_GMC;
	int ARG_INTERLACING;
	int ARG_QPEL;
	int ARG_TURBO;
	int ARG_VHQMODE;
	int ARG_BVHQ;
	int ARG_CLOSED_GOP;
	int ARG_CHROMAME;
	int ARG_PAR;
	int ARG_PARHEIGHT;
	int ARG_PARWIDTH;
	int ARG_QUANTS[6];
	int ARG_FRAMEDROP;
	double ARG_CQ;
	int ARG_FULL1PASS;
	int ARG_REACTION;
	int ARG_AVERAGING;
	int ARG_SMOOTHER;
	int ARG_KBOOST;
	int ARG_KREDUCTION;
	int ARG_KTHRESH;
	int ARG_CHIGH;
	int ARG_CLOW;
	int ARG_OVERSTRENGTH;
	int ARG_OVERIMPROVE;
	int ARG_OVERDEGRADE;
	int ARG_OVERHEAD;
	int ARG_VBVSIZE;
	int ARG_VBVMAXRATE;//VBV(video buffer verifier)
	int ARG_VBVPEAKRATE;
	int ARG_THREADS;
	int ARG_VFR;
	int ARG_PROGRESS;
	int ARG_COLORSPACE;
	char filepath[256];
	void *enc_handle;
	unsigned char qmatrix_intra[64];
	unsigned char qmatrix_inter[64];
	int height_ratios[6];
	int width_ratios[6];
}encparam;

const char userdata_start_code[] = "\0\0\x01\xb2";

class CEncoderXvid
{
public:
	CEncoderXvid(int width, int height, int colorspace, int framerate);
	~CEncoderXvid();
	int enc_init(int use_assembler);
	int enc_stop();
	int enc_main(unsigned char *image, unsigned char *bitstream, int *key, int *stats_type, int *stats_quant, int *stats_length, int framenum);
	void removedivxp(char *buf, int size);
	XvidParam xvidparam;
protected:
private:
	void apply_zone_modifiers(xvid_enc_frame_t *frame, int framenum);
	void prepare_cquant_zones();
	void sort_zones(zone_t * zones, int zone_num, int * sel);
};

#endif

