#include "XvidEncoder.h"


CEncoderXvid::CEncoderXvid(int width, int height, int colorspace,int framerate)
{
	xvidparam.width = width;
	xvidparam.height = height;
	if(width<=352)
	{
		xvidparam.bitrate = 768000;
	}
	else
	{
		xvidparam.bitrate = 2048000;
	}
	xvidparam.framerate = framerate;
	xvidparam.gop = 100;
	xvidparam.colorspace = colorspace;
	xvidparam.NUM_ZONES = 0;
	xvidparam.ARG_STATS = 0;
	xvidparam.ARG_SSIM = -1;
	xvidparam.ARG_SSIM_PATH = NULL;
	xvidparam.ARG_DUMP = 0;
	xvidparam.ARG_LUMIMASKING = 0;
	xvidparam.ARG_SINGLE = 1;
	xvidparam.ARG_QUALITY = 6;
	xvidparam.ARG_DWRATE = 25;
	xvidparam.ARG_DWSCALE = 1;
	xvidparam.ARG_MAXFRAMENR = ABS_MAXFRAMENR;
	xvidparam.ARG_STARTFRAMENR = 0;
	xvidparam.ARG_INPUTTYPE = 0;
	xvidparam.ARG_SAVEMPEGSTREAM = 0;
	xvidparam.ARG_TIMECODEFILE = NULL;
	xvidparam.ARG_BQRATIO = 150;
	xvidparam.ARG_BQOFFSET = 100;
	xvidparam.ARG_MAXBFRAMES = 0;
	xvidparam.ARG_PACKED = 1;
	xvidparam.ARG_DEBUG = 0;
	xvidparam.ARG_VOPDEBUG = 0;
	xvidparam.ARG_TRELLIS = 1;
	xvidparam.ARG_QTYPE = 0;
	xvidparam.ARG_QMATRIX = 0;
	xvidparam.ARG_GMC = 0;
	xvidparam.ARG_INTERLACING = 0;
	xvidparam.ARG_QPEL = 1;
	xvidparam.ARG_TURBO = 0;
	xvidparam.ARG_VHQMODE = 1;
	xvidparam.ARG_BVHQ = 0;
	xvidparam.ARG_CLOSED_GOP = 1;
	xvidparam.ARG_CHROMAME = 1;
	xvidparam.ARG_PAR = 1;
	xvidparam.ARG_PARHEIGHT;
	xvidparam.ARG_PARWIDTH;
	xvidparam.ARG_QUANTS[0] = 2;
	xvidparam.ARG_QUANTS[1] = 31;
	xvidparam.ARG_QUANTS[2] = 2;
	xvidparam.ARG_QUANTS[3] = 31;
	xvidparam.ARG_QUANTS[4] = 2;
	xvidparam.ARG_QUANTS[5] = 31;

	xvidparam.ARG_FRAMEDROP = 0;
	xvidparam.ARG_CQ = 0;
	xvidparam.ARG_FULL1PASS = 0;
	xvidparam.ARG_REACTION = 16;
	xvidparam.ARG_AVERAGING = 100;
	xvidparam.ARG_SMOOTHER = 100;
	xvidparam.ARG_KBOOST = 10;
	xvidparam.ARG_KREDUCTION = 20;
	xvidparam.ARG_KTHRESH = 1;
	xvidparam.ARG_CHIGH = 0;
	xvidparam.ARG_CLOW = 0;
	xvidparam.ARG_OVERSTRENGTH = 5;
	xvidparam.ARG_OVERIMPROVE = 5;
	xvidparam.ARG_OVERDEGRADE = 5;
	xvidparam.ARG_OVERHEAD = 0;
	xvidparam.ARG_VBVSIZE = 0;
	xvidparam.ARG_VBVMAXRATE = 0;//VBV(video buffer verifier)
	xvidparam.ARG_VBVPEAKRATE = 0;
	xvidparam.ARG_THREADS = 0;
	xvidparam.ARG_VFR = 0;
	xvidparam.ARG_PROGRESS = 0;
	xvidparam.ARG_COLORSPACE = XVID_CSP_YV12;
	xvidparam.enc_handle = NULL;

	xvidparam.height_ratios[0] = 1;
	xvidparam.height_ratios[1] = 1;
	xvidparam.height_ratios[2] = 11;
	xvidparam.height_ratios[3] = 11;
	xvidparam.height_ratios[4] = 11;
	xvidparam.height_ratios[5] = 33;
	xvidparam.width_ratios[0] = 1;
	xvidparam.width_ratios[1] = 1;
	xvidparam.width_ratios[2] = 12;
	xvidparam.width_ratios[3] = 10;
	xvidparam.width_ratios[4] = 16;
	xvidparam.width_ratios[5] = 40;
}

CEncoderXvid::~CEncoderXvid()
{

}

#define FRAMERATE_INCR 1001

/* Initialize encoder for first use, pass all needed parameters to the codec */
int CEncoderXvid::enc_init(int use_assembler)
{
	int xerr;
	//xvid_plugin_cbr_t cbr;
	xvid_plugin_single_t single;
	xvid_plugin_ssim_t ssim;
	//xvid_plugin_fixed_t rcfixed;
	xvid_enc_plugin_t plugins[8];
	xvid_gbl_init_t xvid_gbl_init;
	xvid_enc_create_t xvid_enc_create;
	int i;

	/*------------------------------------------------------------------------
	* XviD core initialization
	*----------------------------------------------------------------------*/

	/* Set version -- version checking will done by xvidcore */
	memset(&xvid_gbl_init, 0, sizeof(xvid_gbl_init));
	xvid_gbl_init.version = XVID_VERSION;
	xvid_gbl_init.debug = xvidparam.ARG_DEBUG;


	/* Do we have to enable ASM optimizations ? */
	if (use_assembler) 
	{
#ifndef ARCH_IS_IA64
		xvid_gbl_init.cpu_flags = XVID_CPU_FORCE | XVID_CPU_ASM;
#else
		xvid_gbl_init.cpu_flags = 0;
#endif
	} 
	else
	{
		xvid_gbl_init.cpu_flags = XVID_CPU_FORCE;
	}

	/* Initialize XviD core -- Should be done once per __process__ */
	xvid_global(NULL, XVID_GBL_INIT, &xvid_gbl_init, NULL);
//	enc_info();

	/*------------------------------------------------------------------------
	* XviD encoder initialization
	*----------------------------------------------------------------------*/

	/* Version again */
	memset(&xvid_enc_create, 0, sizeof(xvid_enc_create));
	xvid_enc_create.version = XVID_VERSION;

	/* Width and Height of input frames */
	xvid_enc_create.width = xvidparam.width;
	xvid_enc_create.height = xvidparam.height;
	xvid_enc_create.profile = 0xf5; /* Unrestricted */

	xvid_enc_create.plugins = plugins;
	xvid_enc_create.num_plugins = 0;

	if (xvidparam.ARG_SINGLE)
	{
		memset(&single, 0, sizeof(xvid_plugin_single_t));
		single.version = XVID_VERSION;
		single.bitrate = xvidparam.bitrate;
		single.reaction_delay_factor = xvidparam.ARG_REACTION;
		single.averaging_period = xvidparam.ARG_AVERAGING;
		single.buffer = xvidparam.ARG_SMOOTHER;
		plugins[xvid_enc_create.num_plugins].func = xvid_plugin_single;
		plugins[xvid_enc_create.num_plugins].param = &single;
		xvid_enc_create.num_plugins++;
		if (!xvidparam.bitrate)
			prepare_cquant_zones();
	}

	/* Zones stuff */
	xvid_enc_create.zones = (xvid_enc_zone_t*)malloc(sizeof(xvid_enc_zone_t) * xvidparam.NUM_ZONES);
	xvid_enc_create.num_zones = xvidparam.NUM_ZONES;
	for (i=0; i < xvid_enc_create.num_zones; i++) 
	{
		xvid_enc_create.zones[i].frame = xvidparam.ZONES[i].frame;
		xvid_enc_create.zones[i].base = 100;
		xvid_enc_create.zones[i].mode = xvidparam.ZONES[i].mode;
		xvid_enc_create.zones[i].increment = xvidparam.ZONES[i].modifier;
	}

	if (xvidparam.ARG_LUMIMASKING)
	{
		plugins[xvid_enc_create.num_plugins].func = xvid_plugin_lumimasking;
		plugins[xvid_enc_create.num_plugins].param = NULL;
		xvid_enc_create.num_plugins++;
	}

	if (xvidparam.ARG_DUMP)
	{
		plugins[xvid_enc_create.num_plugins].func = xvid_plugin_dump;
		plugins[xvid_enc_create.num_plugins].param = NULL;
		xvid_enc_create.num_plugins++;
	}

	if (xvidparam.ARG_SSIM>=0 || xvidparam.ARG_SSIM_PATH != NULL)
	{
		memset(&ssim, 0, sizeof(xvid_plugin_ssim_t));
		plugins[xvid_enc_create.num_plugins].func = xvid_plugin_ssim;
		if( xvidparam.ARG_SSIM >=0)
		{
			ssim.b_printstat = 1;
			ssim.acc = xvidparam.ARG_SSIM;
		} 
		else 
		{
			ssim.b_printstat = 0;
			ssim.acc = 2;
		}

		if(xvidparam.ARG_SSIM_PATH != NULL)
		{		
			ssim.stat_path = xvidparam.ARG_SSIM_PATH;
		}

		ssim.cpu_flags = xvid_gbl_init.cpu_flags;
		ssim.b_visualize = 0;
		plugins[xvid_enc_create.num_plugins].param = &ssim;
		xvid_enc_create.num_plugins++;
	}

	xvid_enc_create.num_threads = xvidparam.ARG_THREADS;

	/* Frame rate  */
	xvid_enc_create.fincr = xvidparam.ARG_DWSCALE;
	xvid_enc_create.fbase = xvidparam.ARG_DWRATE;

	/* Maximum key frame interval */
	if (xvidparam.gop > 0)
	{
		xvid_enc_create.max_key_interval = xvidparam.gop;
	}
	else 
	{
		xvid_enc_create.max_key_interval = (int) xvidparam.framerate *10;
	}

	xvid_enc_create.min_quant[0]=xvidparam.ARG_QUANTS[0];
	xvid_enc_create.min_quant[1]=xvidparam.ARG_QUANTS[2];
	xvid_enc_create.min_quant[2]=xvidparam.ARG_QUANTS[4];
	xvid_enc_create.max_quant[0]=xvidparam.ARG_QUANTS[1];
	xvid_enc_create.max_quant[1]=xvidparam.ARG_QUANTS[3];
	xvid_enc_create.max_quant[2]=xvidparam.ARG_QUANTS[5];

	/* Bframes settings */
	xvid_enc_create.max_bframes = xvidparam.ARG_MAXBFRAMES;
	xvid_enc_create.bquant_ratio = xvidparam.ARG_BQRATIO;
	xvid_enc_create.bquant_offset = xvidparam.ARG_BQOFFSET;

	/* Frame drop ratio */
	xvid_enc_create.frame_drop_ratio = xvidparam.ARG_FRAMEDROP;

	/* Global encoder options */
	xvid_enc_create.global = 0;

	if (xvidparam.ARG_PACKED)
		xvid_enc_create.global |= XVID_GLOBAL_PACKED;

	if (xvidparam.ARG_CLOSED_GOP)
		xvid_enc_create.global |= XVID_GLOBAL_CLOSED_GOP;

	if (xvidparam.ARG_STATS)
		xvid_enc_create.global |= XVID_GLOBAL_EXTRASTATS_ENABLE;

	/* I use a small value here, since will not encode whole movies, but short clips */
	xerr = xvid_encore(NULL, XVID_ENC_CREATE, &xvid_enc_create, NULL);

	/* Retrieve the encoder instance from the structure */
	xvidparam.enc_handle = xvid_enc_create.handle;
	free(xvid_enc_create.zones);

	return (xerr);
}

int CEncoderXvid::enc_stop()
{
	int xerr;
	/* Destroy the encoder instance */
	xerr = xvid_encore(xvidparam.enc_handle, XVID_ENC_DESTROY, NULL, NULL);
	return (xerr);
}

int CEncoderXvid::enc_main(unsigned char *image,
		 unsigned char *bitstream,
		 int *key,
		 int *stats_type,
		 int *stats_quant,
		 int *stats_length,
		 int framenum)
{
	int ret;

	xvid_enc_frame_t xvid_enc_frame;
	xvid_enc_stats_t xvid_enc_stats;

	/* Version for the frame and the stats */
	memset(&xvid_enc_frame, 0, sizeof(xvid_enc_frame));
	xvid_enc_frame.version = XVID_VERSION;

	memset(&xvid_enc_stats, 0, sizeof(xvid_enc_stats));
	xvid_enc_stats.version = XVID_VERSION;

	/* Bind output buffer */
	xvid_enc_frame.bitstream = bitstream;
	xvid_enc_frame.length = -1;

	/* Initialize input image fields */
	if (image) 
	{
		xvid_enc_frame.input.plane[0] = image;
#ifndef READ_PNM
		xvid_enc_frame.input.csp = xvidparam.colorspace;
		xvid_enc_frame.input.stride[0] = xvidparam.width;
#else
		xvid_enc_frame.input.csp = XVID_CSP_BGR;
		xvid_enc_frame.input.stride[0] = xvidparam.width*3;
#endif
	} 
	else 
	{
		xvid_enc_frame.input.csp = XVID_CSP_NULL;
	}

	/* Set up core's general features */ 
	xvid_enc_frame.vol_flags = 0;
	if (xvidparam.ARG_STATS)
		xvid_enc_frame.vol_flags |= XVID_VOL_EXTRASTATS;
	if (xvidparam.ARG_QTYPE)
	{
		xvid_enc_frame.vol_flags |= XVID_VOL_MPEGQUANT;
		if (xvidparam.ARG_QMATRIX)
		{
			xvid_enc_frame.quant_intra_matrix = xvidparam.qmatrix_intra;
			xvid_enc_frame.quant_inter_matrix = xvidparam.qmatrix_inter;
		}
		else 
		{
			/* We don't use special matrices */
			xvid_enc_frame.quant_intra_matrix = NULL;
			xvid_enc_frame.quant_inter_matrix = NULL;
		}
	}

	if (xvidparam.ARG_PAR)
		xvid_enc_frame.par = xvidparam.ARG_PAR;
	else 
	{
		xvid_enc_frame.par = XVID_PAR_EXT;
		xvid_enc_frame.par_width = xvidparam.ARG_PARWIDTH;
		xvid_enc_frame.par_height = xvidparam.ARG_PARHEIGHT;
	}

	if (xvidparam.ARG_QPEL)
	{
		xvid_enc_frame.vol_flags |= XVID_VOL_QUARTERPEL;
		xvid_enc_frame.motion |= XVID_ME_QUARTERPELREFINE16 | XVID_ME_QUARTERPELREFINE8;
	}
	if (xvidparam.ARG_GMC)
	{
		xvid_enc_frame.vol_flags |= XVID_VOL_GMC;
		xvid_enc_frame.motion |= XVID_ME_GME_REFINE;
	}

	/* Set up core's general features */
	xvid_enc_frame.vop_flags = vop_presets[xvidparam.ARG_QUALITY];

	if (xvidparam.ARG_INTERLACING)
	{
		xvid_enc_frame.vol_flags |= XVID_VOL_INTERLACING;
		if (xvidparam.ARG_INTERLACING == 2)
			xvid_enc_frame.vop_flags |= XVID_VOP_TOPFIELDFIRST;
	}

	xvid_enc_frame.vop_flags |= XVID_VOP_HALFPEL;
	xvid_enc_frame.vop_flags |= XVID_VOP_HQACPRED;

	if (xvidparam.ARG_VOPDEBUG)
	{
		xvid_enc_frame.vop_flags |= XVID_VOP_DEBUG;
	}

	if (xvidparam.ARG_TRELLIS)
	{
		xvid_enc_frame.vop_flags |= XVID_VOP_TRELLISQUANT;
	}

	/* Frame type -- taken from function call parameter */
	/* Sometimes we might want to force the last frame to be a P Frame */
	xvid_enc_frame.type = *stats_type;

	/* Force the right quantizer -- It is internally managed by RC plugins */
	xvid_enc_frame.quant = 0;

	if (xvidparam.ARG_CHROMAME)
		xvid_enc_frame.motion |= XVID_ME_CHROMA_PVOP + XVID_ME_CHROMA_BVOP;

	/* Set up motion estimation flags */
	xvid_enc_frame.motion |= motion_presets[xvidparam.ARG_QUALITY];

	if (xvidparam.ARG_TURBO)
		xvid_enc_frame.motion |= XVID_ME_FASTREFINE16 | XVID_ME_FASTREFINE8 | 
		XVID_ME_SKIP_DELTASEARCH | XVID_ME_FAST_MODEINTERPOLATE | 
		XVID_ME_BFRAME_EARLYSTOP;

	if (xvidparam.ARG_BVHQ)
		xvid_enc_frame.vop_flags |= XVID_VOP_RD_BVOP;

	switch (xvidparam.ARG_VHQMODE) /* this is the same code as for vfw */
	{
	case 1: /* VHQ_MODE_DECISION */
		xvid_enc_frame.vop_flags |= XVID_VOP_MODEDECISION_RD;
		break;

	case 2: /* VHQ_LIMITED_SEARCH */
		xvid_enc_frame.vop_flags |= XVID_VOP_MODEDECISION_RD;
		xvid_enc_frame.motion |= XVID_ME_HALFPELREFINE16_RD;
		xvid_enc_frame.motion |= XVID_ME_QUARTERPELREFINE16_RD;
		break;

	case 3: /* VHQ_MEDIUM_SEARCH */
		xvid_enc_frame.vop_flags |= XVID_VOP_MODEDECISION_RD;
		xvid_enc_frame.motion |= XVID_ME_HALFPELREFINE16_RD;
		xvid_enc_frame.motion |= XVID_ME_HALFPELREFINE8_RD;
		xvid_enc_frame.motion |= XVID_ME_QUARTERPELREFINE16_RD;
		xvid_enc_frame.motion |= XVID_ME_QUARTERPELREFINE8_RD;
		xvid_enc_frame.motion |= XVID_ME_CHECKPREDICTION_RD;
		break;

	case 4: /* VHQ_WIDE_SEARCH */
		xvid_enc_frame.vop_flags |= XVID_VOP_MODEDECISION_RD;
		xvid_enc_frame.motion |= XVID_ME_HALFPELREFINE16_RD;
		xvid_enc_frame.motion |= XVID_ME_HALFPELREFINE8_RD;
		xvid_enc_frame.motion |= XVID_ME_QUARTERPELREFINE16_RD;
		xvid_enc_frame.motion |= XVID_ME_QUARTERPELREFINE8_RD;
		xvid_enc_frame.motion |= XVID_ME_CHECKPREDICTION_RD;
		xvid_enc_frame.motion |= XVID_ME_EXTSEARCH_RD;
		break;

	default :
		break;
	}
	/* Not sure what this does */
	// force keyframe spacing in 2-pass 1st pass
	if (xvidparam.ARG_QUALITY == 0)
		xvid_enc_frame.type = XVID_TYPE_IVOP;
	/* frame-based stuff */
	apply_zone_modifiers(&xvid_enc_frame, framenum);
	/* Encode the frame */
	ret = xvid_encore(xvidparam.enc_handle, XVID_ENC_ENCODE, &xvid_enc_frame, &xvid_enc_stats);
	*key = (xvid_enc_frame.out_flags & XVID_KEYFRAME);
	*stats_type = xvid_enc_stats.type;
	*stats_quant = xvid_enc_stats.quant;
	*stats_length = xvid_enc_stats.length;
	return (ret);
}

void CEncoderXvid::sort_zones(zone_t * zones, int zone_num, int * sel)
{
	int i, j;
	zone_t tmp;
	for (i = 0; i < zone_num; i++) 
	{
		int cur = i;
		int min_f = zones[i].frame;
		for (j = i + 1; j < zone_num; j++) 
		{
			if (zones[j].frame < min_f) 
			{
				min_f = zones[j].frame;
				cur = j;
			}
		}
		if (cur != i) 
		{
			tmp = zones[i];
			zones[i] = zones[cur];
			zones[cur] = tmp;
			if (i == *sel) *sel = cur;
			else if (cur == *sel) *sel = i;
		}
	}
}

/* constant-quant zones for fixed quant encoding */
void CEncoderXvid::prepare_cquant_zones() 
{
	int i = 0;
	if (xvidparam.NUM_ZONES == 0 || xvidparam.ZONES[0].frame != 0)
	{
		/* first zone does not start at frame 0 or doesn't exist */
		if (xvidparam.NUM_ZONES >= MAX_ZONES) xvidparam.NUM_ZONES--; /* we sacrifice last zone */

		xvidparam.ZONES[xvidparam.NUM_ZONES].frame = 0;
		xvidparam.ZONES[xvidparam.NUM_ZONES].mode = XVID_ZONE_QUANT;
		xvidparam.ZONES[xvidparam.NUM_ZONES].modifier = xvidparam.ARG_CQ;
		xvidparam.ZONES[xvidparam.NUM_ZONES].type = XVID_TYPE_AUTO;
		xvidparam.ZONES[xvidparam.NUM_ZONES].greyscale = 0;
		xvidparam.ZONES[xvidparam.NUM_ZONES].chroma_opt = 0;
		xvidparam.ZONES[xvidparam.NUM_ZONES].bvop_threshold = 0;
		xvidparam.ZONES[xvidparam.NUM_ZONES].cartoon_mode = 0;
		xvidparam.NUM_ZONES++;

		sort_zones(xvidparam.ZONES, xvidparam.NUM_ZONES, &i);
	}

	/* step 2: let's change all weight zones into quant zones */

	for(i = 0; i < xvidparam.NUM_ZONES; i++)
		if (xvidparam.ZONES[i].mode == XVID_ZONE_WEIGHT)
		{
			xvidparam.ZONES[i].mode = XVID_ZONE_QUANT;
			xvidparam.ZONES[i].modifier = (100*xvidparam.ARG_CQ) / xvidparam.ZONES[i].modifier;
		}
}

void CEncoderXvid::apply_zone_modifiers(xvid_enc_frame_t * frame, int framenum)
{
	int i;

	for (i=0; i<xvidparam.NUM_ZONES && xvidparam.ZONES[i].frame <= framenum; i++) ;

	if (--i < 0) return; /* there are no zones, or we're before the first zone */

	if (framenum == xvidparam.ZONES[i].frame)
		frame->type = xvidparam.ZONES[i].type;

	if (xvidparam.ZONES[i].greyscale) {
		frame->vop_flags |= XVID_VOP_GREYSCALE;
	}

	if (xvidparam.ZONES[i].chroma_opt) {
		frame->vop_flags |= XVID_VOP_CHROMAOPT;
	}

	if (xvidparam.ZONES[i].cartoon_mode) {
		frame->vop_flags |= XVID_VOP_CARTOON;
		frame->motion |= XVID_ME_DETECT_STATIC_MOTION;
	}

	if (xvidparam.ARG_MAXBFRAMES)
	{
		frame->bframe_threshold = xvidparam.ZONES[i].bvop_threshold;
	}
}

void CEncoderXvid::removedivxp(char *buf, int bufsize) 
{
	int i;
	char* userdata;

	for (i=0; i <= (bufsize-sizeof(userdata_start_code)); i++) 
	{
		if (memcmp((void*)userdata_start_code, (void*)(buf+i), strlen(userdata_start_code))==0) 
		{
			if ((userdata = strstr(buf+i+4, "DivX"))!=NULL) 
			{
				userdata[strlen(userdata)-1] = '\0';
				return;
			}
		}
	}
}




