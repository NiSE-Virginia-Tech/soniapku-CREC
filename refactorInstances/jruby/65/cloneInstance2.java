    public IRubyObject call(ThreadContext context, IRubyObject caller, IRubyObject self, IRubyObject[] args, Block block) {
        RubyClass selfType = getClass(self);
        StaticScope refinedScope = context.getCurrentStaticScope();
        Map<RubyClass, RubyModule> refinements;
        RubyModule refinement;
        DynamicMethod method = null;

        while (refinedScope != null &&
                (
                        (refinements = refinedScope.getOverlayModule(context).getRefinements()).isEmpty() ||
                                (refinement = refinements.get(selfType)) == null ||
                                (method = refinement.searchMethod(methodName)).isUndefined())
                ) {
            refinedScope = refinedScope.getPreviousCRefScope();
        }

        if (refinedScope == null) {
            return super.call(context, caller, self);
        }

        return method.call(context, self, selfType, methodName, args, block);
    }
