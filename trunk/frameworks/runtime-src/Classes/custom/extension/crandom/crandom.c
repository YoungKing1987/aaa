
#ifdef __cplusplus
extern "C" {
#endif
#include "lua.h"
#include "lualib.h"
#include "lauxlib.h"
#ifdef __cplusplus
}
#endif

#include "crandom.h"

#if LUA_VERSION_NUM == 501

#define lua_rawlen lua_objlen
#define luaL_newlib(L ,reg) luaL_register(L, "crandom", reg)
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

//////////////////////////////////////////////////////////////////////////
// 线性同余算法

uint64_t  m_rlc_seed = 0;

void rlc_seed(uint32_t seed)
{
	m_rlc_seed = seed;
}
uint32_t rlc_rand()
{
	m_rlc_seed = (m_rlc_seed * 0x5deece66dULL + 11) & 0xffffffffffffULL;
	return (uint32_t)(m_rlc_seed >> 16);
}

float rlc_real()
{
	return (rlc_rand()*(1.0f / 4294967295.0f));
}

static int _rlc_seed(lua_State *L) 
{
	m_rlc_seed = luaL_optinteger(L, 1, 0);
	return 0;
}

static int _rlc_rand(lua_State *L)
{
	lua_pushinteger(L, rlc_rand());
	return 1;
}

// return [nlow, nhigh]
static int _rlc_between(lua_State *L)
{
	uint32_t nlow = luaL_optinteger(L, 1, 0);
	uint32_t nhigh = luaL_optinteger(L, 2, 0);
	if (nhigh < nlow)
	{
		lua_pushinteger(L, 0);
		return 1;
	}

	lua_pushinteger(L, rlc_rand() % (nhigh - nlow + 1) + nlow);
	return 1;
}

// return [0,1]
static int _rlc_real(lua_State *L)
{
	lua_pushnumber(L, (rlc_rand()*(1.0 / 4294967295.0)));
	return 1;
}

static int _rlc_bytes(lua_State *L)
{
	uint32_t nsize = luaL_optinteger(L, 1, 0);
	if (nsize == 0) { return 0; }

	uint32_t i = 0;
	unsigned char* buf = (unsigned char*)malloc(nsize);
	for (i = 0; i < nsize; ++i)
	{
		buf[i] = rlc_rand() % 256;
	}
	
	lua_pushlstring(L, (char*)buf, nsize);
	free(buf);
	return 1;
}

//////////////////////////////////////////////////////////////////////////
// http://www.math.sci.hiroshima-u.ac.jp/~m-mat/MT/MT2002/emt19937ar.html

/* Period parameters */
#define N 624
#define M 397
#define MATRIX_A 0x9908b0dfUL   /* constant vector a */
#define UPPER_MASK 0x80000000UL /* most significant w-r bits */
#define LOWER_MASK 0x7fffffffUL /* least significant r bits */

//static unsigned long mt[N]; /* the array for the state vector  */
//static int mti = N + 1; /* mti==N+1 means mt[N] is not initialized */
unsigned long* mt = NULL; /* the array for the state vector  */
int mti = N + 1; /* mti==N+1 means mt[N] is not initialized */

/* initializes mt[N] with a seed */
void init_genrand(unsigned long s)
{
	if (mt == NULL) { mt = (unsigned long*)malloc(N * sizeof(unsigned long)); }

	mt[0] = s & 0xffffffffUL;
	for (mti = 1; mti < N; mti++) {
		mt[mti] =
			(1812433253UL * (mt[mti - 1] ^ (mt[mti - 1] >> 30)) + mti);
		/* See Knuth TAOCP Vol2. 3rd Ed. P.106 for multiplier. */
		/* In the previous versions, MSBs of the seed affect   */
		/* only MSBs of the array mt[].                        */
		/* 2002/01/09 modified by Makoto Matsumoto             */
		mt[mti] &= 0xffffffffUL;
		/* for >32 bit machines */
	}
}

/* initialize by an array with array-length */
/* init_key is the array for initializing keys */
/* key_length is its length */
/* slight change for C++, 2004/2/26 */
void init_by_array(unsigned long init_key[], int key_length)
{
	int i, j, k;
	init_genrand(19650218UL);
	i = 1; j = 0;
	k = (N > key_length ? N : key_length);
	for (; k; k--) {
		mt[i] = (mt[i] ^ ((mt[i - 1] ^ (mt[i - 1] >> 30)) * 1664525UL))
			+ init_key[j] + j; /* non linear */
		mt[i] &= 0xffffffffUL; /* for WORDSIZE > 32 machines */
		i++; j++;
		if (i >= N) { mt[0] = mt[N - 1]; i = 1; }
		if (j >= key_length) j = 0;
	}
	for (k = N - 1; k; k--) {
		mt[i] = (mt[i] ^ ((mt[i - 1] ^ (mt[i - 1] >> 30)) * 1566083941UL))
			- i; /* non linear */
		mt[i] &= 0xffffffffUL; /* for WORDSIZE > 32 machines */
		i++;
		if (i >= N) { mt[0] = mt[N - 1]; i = 1; }
	}

	mt[0] = 0x80000000UL; /* MSB is 1; assuring non-zero initial array */
}

/* generates a random number on [0,0xffffffff]-interval */
unsigned long genrand_int32(void)
{
	if (mt == NULL) { mt = (unsigned long*)malloc(N * sizeof(unsigned long)); }

	unsigned long y;
	static unsigned long mag01[2] = { 0x0UL, MATRIX_A };
	/* mag01[x] = x * MATRIX_A  for x=0,1 */

	if (mti >= N) { /* generate N words at one time */
		int kk;

		if (mti == N + 1)   /* if init_genrand() has not been called, */
			init_genrand(5489UL); /* a default initial seed is used */

		for (kk = 0; kk < N - M; kk++) {
			y = (mt[kk] & UPPER_MASK) | (mt[kk + 1] & LOWER_MASK);
			mt[kk] = mt[kk + M] ^ (y >> 1) ^ mag01[y & 0x1UL];
		}
		for (; kk < N - 1; kk++) {
			y = (mt[kk] & UPPER_MASK) | (mt[kk + 1] & LOWER_MASK);
			mt[kk] = mt[kk + (M - N)] ^ (y >> 1) ^ mag01[y & 0x1UL];
		}
		y = (mt[N - 1] & UPPER_MASK) | (mt[0] & LOWER_MASK);
		mt[N - 1] = mt[M - 1] ^ (y >> 1) ^ mag01[y & 0x1UL];

		mti = 0;
	}

	y = mt[mti++];

	/* Tempering */
	y ^= (y >> 11);
	y ^= (y << 7) & 0x9d2c5680UL;
	y ^= (y << 15) & 0xefc60000UL;
	y ^= (y >> 18);

	return y;
}

static int _rmt_seed(lua_State *L)
{
	init_genrand(luaL_optinteger(L, 1, 0));
	return 0;
}

static int _rmt_rand(lua_State *L)
{
	lua_pushinteger(L, (uint32_t)(genrand_int32()));
	return 1;
}

// return [nlow,nhigh]
static int _rmt_between(lua_State *L)
{
	uint32_t nlow = luaL_optinteger(L, 1, 0);
	uint32_t nhigh = luaL_optinteger(L, 2, 0);
	if (nhigh < nlow)
	{
		lua_pushinteger(L, 0);
		return 1;
	}

	lua_pushinteger(L, genrand_int32() % (nhigh - nlow + 1) + nlow);
	return 1;
}

// return [0,1]
static int _rmt_real(lua_State *L)
{
	lua_pushnumber(L, (genrand_int32()*(1.0 / 4294967295.0)));
	return 1;
}

static int _rmt_bytes(lua_State *L)
{
	uint32_t nsize = luaL_optinteger(L, 1, 0);
	if (nsize == 0) { return 0; }

	uint32_t i = 0;
	unsigned char* buf = (unsigned char*)malloc(nsize);
	for (i = 0; i < nsize; ++i)
	{
		buf[i] = genrand_int32() % 256;
	}

	lua_pushlstring(L, (char*)buf, nsize);
	free(buf);
	return 1;
}

void crandom_rmt_seed(unsigned long s)
{
    init_genrand(s);
}

uint32_t crandom_rmt_rand()
{
    return (uint32_t)genrand_int32();
}

extern int luaopen_crandom(lua_State *L) 
{
	luaL_Reg reg[] = 
	{
		{ "rlc_seed", _rlc_seed },
		{ "rlc_rand", _rlc_rand },
		{ "rlc_between", _rlc_between },
		{ "rlc_real", _rlc_real },
		{ "rlc_bytes", _rlc_bytes },

		{ "rmt_seed", _rmt_seed },
		{ "rmt_rand", _rmt_rand },
		{ "rmt_between", _rmt_between },
		{ "rmt_real", _rmt_real },
		{ "rmt_bytes", _rmt_bytes },

		{NULL,NULL},
	};

	luaL_checkversion(L);
	luaL_newlib(L, reg);

	return 1;
}

