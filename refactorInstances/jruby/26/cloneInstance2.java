        try {
            RubyClass selfType = pollAndGetClass(context, self);
            CacheEntry myCache = cache;
            if (myCache.typeOk(selfType)) {
                return myCache.method.call(context, self, selfType, methodName, arg1, arg2, block);
            }
            return cacheAndCall(caller, selfType, block, context, self, arg1, arg2);
        } catch (JumpException.BreakJump bj) {
