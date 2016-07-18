#include "XvidEncoder.h"
#include "string.h"

int main()
{
	int result;
	int key;
	int stats_type;
	int stats_quant;
	int stats_length;
	int inputnum = 0;
	int width = 352;
	int height = 288;
	int framerate = 25;
	int colorspace = XVID_CSP_I420;
	int use_assemble = 1;
	FILE *fp = NULL;
	fp = fopen("test.yuv","rb");
	if(fp == NULL)
	{
		printf("test.yuv open failed\n");
		return (-1);
	}
	FILE *fr = NULL;
	fr = fopen("output.m4v","wb");
	if(fr == NULL)
	{
		printf("output.m4v open failed\n");
		fclose(fp);
		return (-1);
	}
	CEncoderXvid *xvidencoder = NULL;
	xvidencoder = new CEncoderXvid(width,height,colorspace,framerate);
	if(xvidencoder == NULL)
	{
		printf("New CEncoderXvid object failed\n");
		fclose(fp);
		fclose(fr);
		return (-1);
	}
	unsigned char* sourcebuffer = NULL;
	sourcebuffer = (unsigned char*)calloc(1, width*height*3/2);
	if(sourcebuffer == NULL)
	{
		printf("sourcebuffer memory alloc failed\n");
		fclose(fp);
		fclose(fr);
		delete xvidencoder;
		xvidencoder = NULL;
		return (-1);
	}
	unsigned char* xvidbuffer = NULL;
	xvidbuffer = (unsigned char*)calloc(1, width*height*3/2);
	if(xvidbuffer == NULL)
	{
		printf("xvidbuffer memory alloc failed\n");
		fclose(fp);
		fclose(fr);
		delete xvidencoder;
		xvidencoder = NULL;
		free(sourcebuffer);
		sourcebuffer = NULL;
		return (-1);
	}
	result = xvidencoder->enc_init(use_assemble);
	if (result)
	{
		printf(" Encore INIT problem, return value %d\n",result);
		if (xvidencoder->xvidparam.enc_handle)
		{
			result = xvidencoder->enc_stop();
			if (result)
				printf("Encore RELEASE problem return value %d\n",result);
		}
	}
	stats_type = XVID_TYPE_AUTO;
	while(feof(fp) == 0)
	{
		int m4v_size = 0;
		/* Set constant quant to default if no bitrate given for single pass */
		if (xvidencoder->xvidparam.ARG_SINGLE && (!xvidencoder->xvidparam.bitrate) && (!xvidencoder->xvidparam.ARG_CQ))
			xvidencoder->xvidparam.ARG_CQ = DEFAULT_QUANT;
		if (inputnum >= (unsigned int)xvidencoder->xvidparam.ARG_MAXFRAMENR-1 && xvidencoder->xvidparam.ARG_MAXBFRAMES)
		{
			stats_type = XVID_TYPE_PVOP;
		}
		else
			stats_type = XVID_TYPE_AUTO;

		fread(sourcebuffer,1,width*height*3/2,fp);
		m4v_size = xvidencoder->enc_main(sourcebuffer, xvidbuffer, &key, &stats_type,
							 &stats_quant, &stats_length,inputnum);
		if(m4v_size<=0)
		{
			break;
		}
		else
		{
			fwrite(xvidbuffer,1,m4v_size,fr);
		}
		inputnum++;
	}
	fclose(fp);
	fp = NULL;
	fclose(fr);
	fr = NULL;
	free(sourcebuffer);
	sourcebuffer = NULL;
	free(xvidbuffer);
	xvidbuffer = NULL;
	if (xvidencoder->xvidparam.enc_handle!=NULL)
	{
		int result = xvidencoder->enc_stop();
		if (result)
			printf("Encore RELEASE problem return value %d\n",result);
	}
	delete xvidencoder;
	xvidencoder = NULL;
	return 0;
}

