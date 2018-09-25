
#include "encode/CCEncode.h"
#include "scripting/lua-bindings/manual/CCLuaEngine.h"

extern "C" {
#include "encode/base64/libbase64.h"
#include "encode/md5/md5.h"
}

extern "C" {
#include "lua.h"
#include "scripting/lua-bindings/manual/tolua_fix.h"
}

NS_CC_EXTRA_BEGIN

int CCEncode::encodeBase64Len(const char* input, int inputLength)
{
    return Base64encode_len(inputLength);
}

int CCEncode::encodeBase64(const char* input,
                           int inputLength,
                           char* output,
                           int outputBufferLength)
{
    CCAssert(Base64encode_len(inputLength) <= outputBufferLength, "CCEncode::encodeBase64() - outputBufferLength too small");
    return Base64encode(output, input, inputLength);
}

int CCEncode::decodeBase64Len(const char* input)
{
    return Base64decode_len(input);
}

int CCEncode::decodeBase64(const char* input,
                           char* output,
                           int outputBufferLength)
{
    CCAssert(Base64decode_len(input) <= outputBufferLength, "CCEncode::decodeBase64() - outputBufferLength too small");
    return Base64decode(output, input);
}

void CCEncode::MD5(void* input, int inputLength, unsigned char* output)
{
    MD5_CTX ctx;
    Encode_MD5_Init(&ctx);
    Encode_MD5_Update(&ctx, input, inputLength);
    Encode_MD5_Final(output, &ctx);
}

void CCEncode::MD5File(const char* path, unsigned char* output)
{
    FILE *file = fopen(path, "rb");
    if (file == NULL)
        return;
    
    MD5_CTX ctx;
    Encode_MD5_Init(&ctx);
    
    int i;
    const int BUFFER_SIZE = 1024;
    char buffer[BUFFER_SIZE];
    while ((i = fread(buffer, 1, BUFFER_SIZE, file)) > 0) {
        Encode_MD5_Update(&ctx, buffer, (unsigned) i);
    }
    
    fclose(file);
    Encode_MD5_Final(output, &ctx);
}

const string CCEncode::MD5String(void* input, int inputLength)
{
    unsigned char buffer[MD5_BUFFER_LENGTH];
    MD5(static_cast<void*>(input), inputLength, buffer);

    LuaStack* stack = LuaEngine::getInstance()->getLuaStack();
    stack->clean();

    char* hex = bin2hex(buffer, MD5_BUFFER_LENGTH);
    string ret(hex);
    delete[] hex;
    return ret;
}

LUA_STRING CCEncode::encodingBase64Lua(bool isDecoding,
                                       const char* input,
                                       int inputLength)
{
    LuaStack* stack = LuaEngine::getInstance()->getLuaStack();
    stack->clean();

    int bufferSize = isDecoding ? Base64decode_len(input) : Base64encode_len(inputLength);
    char *buffer = bufferSize ? (char*)malloc(bufferSize) : NULL;
    int size = 0;

    if (buffer)
    {
        size = isDecoding ? Base64decode(buffer, input) : Base64encode(buffer, input, inputLength);
    }
    if (size)
    {
        stack->pushString(buffer, size);
    }
    else
    {
        stack->pushNil();
    }
    if (buffer)
    {
        free(buffer);
    }
    return 1;
}

LUA_STRING CCEncode::MD5Lua(char* input, bool isRawOutput)
{
    unsigned char buffer[MD5_BUFFER_LENGTH];
    MD5(static_cast<void*>(input), (int)strlen(input), buffer);
    
    LuaStack* stack = LuaEngine::getInstance()->getLuaStack();
    stack->clean();
    
    if (isRawOutput)
    {
        stack->pushString((char*)buffer, MD5_BUFFER_LENGTH);
    }
    else
    {
        char* hex = bin2hex(buffer, MD5_BUFFER_LENGTH);
        stack->pushString(hex);
        delete[] hex;
    }
    
    return 1;
}

LUA_STRING CCEncode::MD5FileLua(const char* path)
{
    unsigned char buffer[MD5_BUFFER_LENGTH];
    MD5File(path, buffer);

    LuaStack* stack = LuaEngine::getInstance()->getLuaStack();
    stack->clean();
    
    char* hex = bin2hex(buffer, MD5_BUFFER_LENGTH);
    stack->pushString(hex);
    delete[] hex;
    
    return 1;
}

char* CCEncode::bin2hex(unsigned char* bin, int binLength)
{
    static const char* hextable = "0123456789abcdef";
    
    int hexLength = binLength * 2 + 1;
    char* hex = new char[hexLength];
    memset(hex, 0, sizeof(char) * hexLength);
    
    int ci = 0;
    for (int i = 0; i < 16; ++i)
    {
        unsigned char c = bin[i];
        hex[ci++] = hextable[(c >> 4) & 0x0f];
        hex[ci++] = hextable[c & 0x0f];
    }
    
    return hex;
}

NS_CC_EXTRA_END
