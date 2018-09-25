

#ifndef _XOR_CRYPTO_H_
#define _XOR_CRYPTO_H_

#include <stddef.h> /* for size_t & NULL declarations */
#include <stdint.h>

#ifdef __cplusplus
extern "C"{
#endif


int32_t xor_encrypt(uint8_t* data, int32_t data_len, uint8_t* key, int32_t key_len);
int32_t xor_decrypt(uint8_t* data, int32_t data_len, uint8_t* key, int32_t key_len);
const char* french_clef(uint32_t ctyp);

#ifdef __cplusplus
}
#endif

#endif
