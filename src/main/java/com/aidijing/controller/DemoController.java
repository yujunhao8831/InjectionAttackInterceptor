package com.aidijing.controller;

import com.aidijing.annotation.PassInjectionAttackIntercept;
import com.aidijing.domain.User;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * @author : 披荆斩棘
 * @date : 2017/9/1
 */
@RestController
public class DemoController {

	private List< User > users = new ArrayList<>(
		Arrays.asList(
			new User().setId( 1L ).setUsername( "admin" ).setPassword( "123456" ) ,
			new User().setId( 2L ).setUsername( "普罗米修斯" ).setPassword( "123456" ) ,
			new User().setId( 3L ).setUsername( "披荆斩棘" ).setPassword( "123456" ) ,
			new User().setId( 4L ).setUsername( "地精风险投资公司" ).setPassword( "123456" ) ,
			new User().setId( 5L ).setUsername( "德鲁伊" ).setPassword( "123456" )
		)
	);

	@GetMapping( "pass-injection-attack-intercept" )
	@PassInjectionAttackIntercept
	public ResponseEntity< String > passInjectionAttackIntercept ( @RequestParam( required = false ) String name ) {
		return ResponseEntity.ok().body( "你好," + name + "!" );
	}

	@GetMapping( "pass-injection-attack-intercept-select" )
	@PassInjectionAttackIntercept( { "update" , "delete" } )
	public ResponseEntity< String > passInjectionAttackInterceptSelect ( @RequestParam( required = false ) String name ) {
		return ResponseEntity.ok().body( "你好," + name + "!" );
	}

	@GetMapping( "users/{name}" )
	@PassInjectionAttackIntercept( { "update" , "delete" } )
	public ResponseEntity< User > users ( @PathVariable String name ) {
		return ResponseEntity.ok()
							 .body( users.parallelStream()
										 .filter( user -> Objects.equals( user.getUsername() , name ) )
										 .findAny()
										 .orElse( new User() ) );
	}

	@PostMapping( "users" )
	@PassInjectionAttackIntercept( { "update" , "delete" } )
	public ResponseEntity< User > users ( @RequestBody User user ) {
		users.add( user.setId( System.currentTimeMillis() ) );
		return ResponseEntity.ok().body( user );
	}

}
