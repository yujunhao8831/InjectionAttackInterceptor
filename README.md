# InjectionAttackInterceptor
注入工具拦截器


``` java


    控制器中加入注解@PassInjectionAttackIntercept,那么这个Mapping则不会进行注入拦截处理,或者对某些特定字符忽略
    // 表示该请求,不会进行注入攻击拦截处理
    @RequestMapping( "demo" )
    @PassInjectionAttackIntercept
    public ResponseEntity< String > demo () {
        return ResponseEntity.ok();
    }

    // 表示该请求,进行注入攻击拦截处理时,如果请求参数中包含了 "update" 或者 "exec",那么对此进行忽略,排除这些关键字符
    @RequestMapping( "demo2" )
    @PassInjectionAttackIntercept( { "update" , "exec" } )
    public ResponseEntity< String > demo () {
        return ResponseEntity.ok();
    }

  // 注解在控制器方法上同理

```
