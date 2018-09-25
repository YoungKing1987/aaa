
#ifndef __CC_EXTENSION_CCENCODE_H_
#define __CC_EXTENSION_CCENCODE_H_

#include "cocos2dx_extra_ex.h"

#if CC_LUA_ENGINE_ENABLED > 0
#include "scripting/lua-bindings/manual/CCLuaEngine.h"
#endif

NS_CC_EXTRA_BEGIN

class CCEncode
{
public:
    static const int MD5_BUFFER_LENGTH = 16;

    /** @brief Get length of encoding data with Base64 algorithm */
    static int encodeBase64Len(const char* input, int inputLength);
    
    /** @brief Encoding data with Base64 algorithm, return encoded string length */
    static int encodeBase64(const char* input, int inputLength,
                            char* output, int outputBufferLength);
    
    /** @brief Get length of Decoding Base 64 */
    static int decodeBase64Len(const char* input);

    /** @brief Decoding Base64 string to data, return decoded data length */
    static int decodeBase64(const char* input,
                            char* output, int outputBufferLength);
    
    /** @brief Calculate MD5, get MD5 code (not string) */
    static void MD5(void* input, int inputLength,
                    unsigned char* output);
    
    static void MD5File(const char* path, unsigned char* output);
    
    static const string MD5String(void* input, int inputLength);
    
    
    /** @brief Encoding data with Base64 algorithm, return encoded string */
	static LUA_STRING encodeBase64Lua(const char* input, int inputLength)
    {
        return encodingBase64Lua(false, input, inputLength);
    }
    
    /** @brief Decoding Base64 string to data, return decoded data length */
	static LUA_STRING decodeBase64Lua(const char* input)
    {
        return encodingBase64Lua(true, input, (int)strlen(input));
    }
    
    /** @brief Calculate MD5, return MD5 string */
	static LUA_STRING MD5Lua(char* input, bool isRawOutput);

	static LUA_STRING MD5FileLua(const char* path);
    
    
private:
    CCEncode(void) {}
    
	static LUA_STRING encodingBase64Lua(bool isDecoding,
                                        const char* input,
                                        int inputLength);
    
    static char* bin2hex(unsigned char* bin, int binLength);
    
};

NS_CC_EXTRA_END

#endif // __CC_EXTENSION_CCENCODE_H_
