
#include <stdlib.h>
#include "xor.h"

#define  CLEF_MAX_NUM  10
const char* hgame_clef[CLEF_MAX_NUM] = {
	",aBOvQ0*(BJ1Wk6W9xa*7SSn",
	"&qY._(UIo?W$h(U3_#d0V+dL",
	"4W&vY#miz&Pm9dbKozoCDtgz",
	"~k7V^=&6gwCY9jM#nqpi^_t7",
	"Vr15((#0`ar.6t/e.dm&j529",
	"lDh=A+y$ds<i:8!%W(lIAp1Tp?$knjC!&N<K",
	"Fvn1?>~6UKKw4gJA8uI>yA!(SF*YLEMcP@W#",
	"IrZ?DW+y8OI4yk<Yn31W/cvbunrlQ6@Us)$S",
	"e67Zzr,:qie~qI+<E2qVmm4lyW5jfSV%xi<M",
	"eqUW@_s71>(+R;Oi^,TUtn8D?etDcG.hb&Yh" 
};

const char* french_clef(uint32_t ctyp)
{
	if (ctyp >= CLEF_MAX_NUM)
	{
		return "";
	}
	
	return hgame_clef[ctyp];
}

int32_t xor_encrypt(uint8_t* data, int32_t data_len, uint8_t* key, int32_t key_len)
{
	int i = 0;
	int j = 0;

	int iStep = sizeof(uint64_t);
	
	// 密钥修正为 iStep整数倍 
	int iKeyLen = key_len + iStep - (key_len + iStep) % iStep;
	uint8_t* szKey = malloc(iKeyLen);
	for (i = 0; i < iKeyLen; ++i)
	{
		szKey[i] = key[j++];
		if (j >= key_len)
		{
			j = 0;
		}
	}
	
	int iInt = data_len / iStep;
	int iLeft = data_len % iStep;
	int iKeyInt = iKeyLen / iStep;

	j = 0;
	for (i = 0; i < iInt; ++i)
	{
		*(uint64_t*)(&data[0] + i * iStep) ^= *(uint64_t*)(&szKey[0] + j * iStep);
		j++;
		if (j >= iKeyInt)
		{
			j = 0;
		}
	}
	
	j = 0;
	for (i = data_len - iLeft; i < data_len; ++i)
	{
		data[i] = data[i] ^ szKey[j++];
		if (j >= iKeyLen)
		{
			j = 0;
		}
	}
	
	free(szKey);
	return 0;
}

int32_t xor_decrypt(uint8_t* data, int32_t data_len, uint8_t* key, int32_t key_len)
{
	return xor_encrypt(data, data_len, key, key_len);
}
