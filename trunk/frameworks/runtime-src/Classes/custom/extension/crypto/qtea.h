
#ifndef _INCLUDED_OTEA_H_
#define _INCLUDED_OTEA_H_

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <time.h>

#ifdef WIN32
#include <stdint.h>
#include <winsock2.h>
#else
#include <netinet/in.h>
#include <sys/time.h>
#include <unistd.h>
typedef char            BOOL;
#endif

typedef unsigned char   BYTE;

#define TRUE 1
#define FALSE 0

#define MD5_DIGEST_LENGTH	16
#define ENCRYPT_PADLEN		18
#define	CRYPT_KEY_SIZE		16
#define MD5_LBLOCK	16


#ifdef __cplusplus
extern "C"{
#endif

int qtea_enc_len(int nInBufLen);


void qtea_encrypt(const BYTE* pInBuf, int32_t nInBufLen, const BYTE* pKey, BYTE* pOutBuf, int32_t *pOutBufLen);

BOOL qtea_decrypt(const BYTE* pInBuf, int32_t nInBufLen, const BYTE* pKey, BYTE* pOutBuf, int32_t *pOutBufLen);


#ifdef __cplusplus
}
#endif

#endif // #ifndef _INCLUDED_OTEA_H_

