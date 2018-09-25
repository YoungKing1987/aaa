
#ifndef __LUA_EXTRA_CRANDOM_H_
#define __LUA_EXTRA_CRANDOM_H_

#if defined(_USRDLL)
#define LUA_EXTENSIONS_DLL     __declspec(dllexport)
#else         /* use a DLL library */
#define LUA_EXTENSIONS_DLL
#endif

#ifdef __cplusplus
extern "C" {
#endif

#ifndef _WIN32
#include <unistd.h>
#include <stdbool.h>
#include <sys/time.h>
#include <sys/utsname.h>
#else
#define alloca _alloca
#include <windows.h>
#endif

#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include <stdint.h>
#include <time.h>

	void LUA_EXTENSIONS_DLL rlc_seed(uint32_t seed);
	uint32_t LUA_EXTENSIONS_DLL rlc_rand();
	float LUA_EXTENSIONS_DLL rlc_real();
	unsigned int LUA_EXTENSIONS_DLL rlc_between(uint32_t min, uint32_t max);
	void crandom_rmt_seed(unsigned long s);
	uint32_t crandom_rmt_rand();
	
#ifdef __cplusplus
}
#endif

#endif /* __LUA_EXTRA_CRANDOM_H_ */
