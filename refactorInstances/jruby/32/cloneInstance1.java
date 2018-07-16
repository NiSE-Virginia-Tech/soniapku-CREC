    public IRubyObject call(ThreadContext context, IRubyObject caller, IRubyObject self, IRubyObject arg1, Block block) {
        RubyClass selfType = pollAndGetClass(context, self);
        try {
            CacheEntry myCache = cache;
            if (myCache.typeOk(selfType)) {
                return myCache.method.call(context, self, selfType, methodName, arg1, block);
            }
            return cacheAndCall(caller, selfType, block, context, self, arg1);
        } catch (JumpException.BreakJump bj) {
            return handleBreakJump(context, bj);
        } catch (JumpException.RetryJump rj) {
            throw retryJumpError(context);
        }
    }
