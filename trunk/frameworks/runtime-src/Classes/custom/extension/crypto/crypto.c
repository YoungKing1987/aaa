#ifdef __cplusplus
extern "C" {
#endif
#include "lua.h"
#include "lualib.h"
#include "lauxlib.h"
#ifdef __cplusplus
}
#endif

#ifndef _MSC_VER
#include <stdbool.h>
#else
#define alloca _alloca
#endif

#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include <stdint.h>

#include "uECC.h"
#include "qtea.h"
#include "xxtea.h"
#include "xor.h"

#undef  _HGAME_SERVER
//#define _HGAME_SERVER  1

#ifdef _HGAME_SERVER
#include <openssl/ssl.h>
#define ECDH_SIZE 33
#define ECDH_MAX_KEY_SIZE 64
#endif


#if LUA_VERSION_NUM == 501

#define lua_rawlen lua_objlen
#define luaL_newlib(L ,reg) luaL_register(L, "ccrypto", reg)
#define luaL_buffinit(L , _ ) 
#define luaL_prepbuffsize( b , cap ) malloc(cap)
#define _Free(p) free(p)
#undef luaL_addsize
#define luaL_addsize(b , len) lua_pushlstring(L, temp , len) ; free(temp)
#define luaL_pushresult(b) 
#define luaL_checkversion(L)

#else

#define _Free(p)

#endif


static int _ecdh_keys(lua_State *L) 
{
	uint8_t private_key[uECC_BYTES];
	uint8_t public_key[uECC_BYTES * 2];

	if (!uECC_make_key(public_key, private_key))
	{
		return 0;
	}

	lua_pushlstring(L, (const char*)public_key, uECC_BYTES * 2);
	lua_pushlstring(L, (const char*)private_key, uECC_BYTES);

	return 2;
}

static int _ecdh_secret(lua_State *L)
{
	size_t private_size = 0;
	size_t public_size = 0;
	uint8_t* public_key = NULL;
	uint8_t* private_key = NULL;

	public_key = (uint8_t*)luaL_checklstring(L, 1, &public_size);
	private_key = (uint8_t*)luaL_checklstring(L, 2, &private_size);

	if (private_key == NULL || public_key == NULL
		|| private_size != uECC_BYTES || public_size != (uECC_BYTES * 2))
	{
		return 0;
	}

	uint8_t secret_key[uECC_BYTES];
	if (!uECC_shared_secret(public_key, private_key, secret_key))
	{
		return 0;
	}
	
	// 与go语言算法实现适配 去掉前面的0
	const char* pdata = (const char*)secret_key;
	int plen = uECC_BYTES;
	int i = 0;
	for (i = 0; i < uECC_BYTES; i++)
	{
		if (secret_key[i] > 0)
		{
			plen = uECC_BYTES - i;
			pdata = (const char*)(&secret_key[i]);
			break;
		}
	}
	
	lua_pushlstring(L, pdata, plen);
	return 1;
}

static int _ecdsa_sign(lua_State *L)
{
	return 0;
}

static int _ecdsa_verify(lua_State *L)
{
	return 0;
}

#ifdef _HGAME_SERVER
static int _ecdh_ssl_keys(lua_State *L)
{
	int nid = luaL_optinteger(L, 1, 0);
	if (nid == 0)
	{
		// NID_X9_62_prime256v1
		nid = NID_secp256k1;
	}

	unsigned char pubkey[ECDH_MAX_KEY_SIZE];

	//Generate Public
	EC_KEY* ecdh_key = EC_KEY_new_by_curve_name(NID_secp256k1);
	EC_KEY_generate_key(ecdh_key);
	const EC_POINT* point = EC_KEY_get0_public_key(ecdh_key);
	const EC_GROUP* group = EC_KEY_get0_group(ecdh_key);

	size_t key_len = EC_POINT_point2oct(group, point, POINT_CONVERSION_COMPRESSED, pubkey, ECDH_MAX_KEY_SIZE, NULL);
	if (0 == key_len)
	{
		EC_KEY_free(ecdh_key);
		return 0;
	}
	else
	{
		lua_pushlightuserdata(L, ecdh_key);
		lua_pushlstring(L, (const char*)pubkey, key_len);
		return 2;
	}
		
}

static int _ecdh_ssl_secret(lua_State *L)
{
	size_t key_len = 0;
	EC_KEY* ecdh_key = (EC_KEY*)lua_touserdata(L, 1);
	unsigned char* peerkey = (unsigned char*)luaL_checklstring(L, 2, &key_len);

	if (ecdh_key == NULL || peerkey == NULL || key_len >= ECDH_MAX_KEY_SIZE)
	{
		return 0;
	}
	
	unsigned char secret_key[ECDH_MAX_KEY_SIZE];
	const EC_GROUP* group = EC_KEY_get0_group(ecdh_key);

	//ComputeKey
	EC_POINT* point_peer = EC_POINT_new(group);
	EC_POINT_oct2point(group, point_peer, peerkey, key_len, NULL);


	size_t secret_len = 0;
	secret_len = ECDH_compute_key(secret_key, ECDH_MAX_KEY_SIZE, point_peer, ecdh_key, NULL);
	if (secret_len == 0)
	{
		return 0;
	}
	
	lua_pushlstring(L, (const char*)secret_key, secret_len);
	return 1;
}

static int _ecdh_ssl_free(lua_State *L)
{
	EC_KEY* ecdh_key = (EC_KEY*)lua_touserdata(L, 1);
	if (ecdh_key == NULL)
	{
		return 0;
	}

	EC_KEY_free(ecdh_key);
	return 0;
}
#endif

static int _enc_xor(lua_State *L)
{
	size_t input_size = 0;
	size_t key_size = 0;
	uint8_t* input = NULL;
	uint8_t* key = NULL;

	input = (uint8_t*)luaL_checklstring(L, 1, &input_size);
	key = (uint8_t*)luaL_checklstring(L, 2, &key_size);

	if (input == NULL || key == NULL
		|| input_size == 0 || key_size == 0)
	{
		return 0;
	}

	uint8_t* ret_buf = (uint8_t*)malloc(input_size);
	memcpy(ret_buf, input, input_size);

	xor_encrypt(ret_buf, input_size, key, key_size);
	lua_pushlstring(L, (const char*)ret_buf, input_size);

	free(ret_buf);
	return 1;
}

static int _dec_xor(lua_State *L)
{
	return _enc_xor(L);
}

static int _clef(lua_State *L)
{
	int ctyp = luaL_optinteger(L, 1, 0);
	const char* pcelf = french_clef(ctyp);
	lua_pushstring(L, pcelf);
	return 1;
}


static int _enc_xxtea(lua_State *L)
{
	size_t input_size = 0;
	size_t key_size = 0;
	uint8_t* input = NULL;
	uint8_t* key = NULL;

	input = (uint8_t*)luaL_checklstring(L, 1, &input_size);
	key = (uint8_t*)luaL_checklstring(L, 2, &key_size);

	if (input == NULL || key == NULL
		|| input_size == 0 || key_size == 0)
	{
		return 0;
	}

	uint8_t* ret = NULL;
	xxtea_long ret_size = 0;
	ret = xxtea_encrypt(input, input_size, key, key_size, &ret_size);

	if (ret)
	{
		lua_pushlstring(L, (const char*)ret, ret_size);
		free(ret);
		return 1;
	}
	else
	{
		free(ret);
		return 0;
	}
}

static int _dec_xxtea(lua_State *L)
{
	size_t input_size = 0;
	size_t key_size = 0;
	uint8_t* input = NULL;
	uint8_t* key = NULL;

	input = (uint8_t*)luaL_checklstring(L, 1, &input_size);
	key = (uint8_t*)luaL_checklstring(L, 2, &key_size);

	if (input == NULL || key == NULL
		|| input_size == 0 || key_size == 0)
	{
		return 0;
	}

	uint8_t* ret = NULL;
	xxtea_long ret_size = 0;
	ret = xxtea_decrypt(input, input_size, key, key_size, &ret_size);

	if (ret)
	{
		lua_pushlstring(L, (const char*)ret, ret_size);
		free(ret);
		return 1;
	}
	else
	{
		free(ret);
		return 0;
	}
}


static int _enc_qtea(lua_State *L)
{
	size_t input_size = 0;
	size_t key_size = 0;
	uint8_t* input = NULL;
	uint8_t* key = NULL;

	input = (uint8_t*)luaL_checklstring(L, 1, &input_size);
	key = (uint8_t*)luaL_checklstring(L, 2, &key_size);

	if (input == NULL || key == NULL
		|| input_size == 0 || key_size == 0)
	{
		return 0;
	}

	int ret_size = qtea_enc_len(input_size);
	uint8_t* ret_buf = (uint8_t*)malloc(ret_size);

	qtea_encrypt(input, input_size, key, ret_buf, &ret_size);

	if (ret_size > 0)
	{
		lua_pushlstring(L, (const char*)ret_buf, ret_size);
		free(ret_buf);
		return 1;
	}
	else
	{
		free(ret_buf);
		return 0;
	}
}

static int _dec_qtea(lua_State *L)
{
	size_t input_size = 0;
	size_t key_size = 0;
	uint8_t* input = NULL;
	uint8_t* key = NULL;

	input = (uint8_t*)luaL_checklstring(L, 1, &input_size);
	key = (uint8_t*)luaL_checklstring(L, 2, &key_size);

	if (input == NULL || key == NULL
		|| input_size == 0 || key_size == 0)
	{
		return 0;
	}

	int ret_size = input_size;
	uint8_t* ret_buf = (uint8_t*)malloc(ret_size);

	if (qtea_decrypt(input, input_size, key, ret_buf, &ret_size) == TRUE)
	{
		lua_pushlstring(L, (const char*)ret_buf, ret_size);
		free(ret_buf);
		return 1;
	}
	else
	{
		free(ret_buf);
		return 0;
	}
}

static int _hash_BKDR(lua_State *L)
{
	size_t input_size = 0;
	char* input = NULL;

	input = (char*)luaL_checklstring(L, 1, &input_size);
	if (NULL == input)
	{
		lua_pushinteger(L, 0);
		return 1;
	}

	unsigned int seed = 131313; // 31 131 1313 13131 131313 etc..
	unsigned int hash = 0;

	while (*input)
	{
		hash = hash * seed + (*input++);
	}

	lua_pushinteger(L, (hash & 0x7FFFFFFF));
	return 1;
}

static int _hash_AP(lua_State *L)
{
	size_t input_size = 0;
	char* input = NULL;

	input = (char*)luaL_checklstring(L, 1, &input_size);
	if (NULL == input)
	{
		lua_pushinteger(L, 0);
		return 1;
	}

	int i = 0;
	unsigned int hash = 0;
	
	for (i = 0; *input; i++)
	{
		if ((i & 1) == 0)
		{
			hash ^= ((hash << 7) ^ (*input++) ^ (hash >> 3));
		}
		else
		{
			hash ^= (~((hash << 11) ^ (*input++) ^ (hash >> 5)));
		}
	}

	lua_pushinteger(L, (hash & 0x7FFFFFFF));
	return 1;
}



#ifdef __cplusplus
extern "C" {
#endif

int
luaopen_ccrypto(lua_State *L) {
	luaL_Reg reg[] = {
		{ "ecdhKey", _ecdh_keys },
		{ "ecdhSecret", _ecdh_secret },
		{ "ecdsaSign", _ecdsa_sign },
		{ "ecdsaVerify", _ecdsa_verify },

		{ "encXor", _enc_xor },
		{ "decXor", _dec_xor },
		{ "clef", _clef },

		{ "encXxtea", _enc_xxtea },
		{ "decXxtea", _dec_xxtea },

		{ "encQtea", _enc_qtea },
		{ "decQtea", _dec_qtea },
		{ "hashBKDR", _hash_BKDR },
		{ "hashAP",   _hash_AP},

#ifdef _HGAME_SERVER
		{ "ecdhSslKey", _ecdh_ssl_keys },
		{ "ecdhSslSecret", _ecdh_ssl_secret },
		{ "ecdhSslFree", _ecdh_ssl_free },
#endif

		{NULL,NULL},
	};

	luaL_checkversion(L);
	luaL_newlib(L, reg);

	return 1;
}

#ifdef __cplusplus
}
#endif
