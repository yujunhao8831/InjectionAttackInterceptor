package com.aidijing.domain;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author : 披荆斩棘
 * @date : 2017/9/1
 */
@Data
@Accessors( chain = true )
public class User implements Serializable {
	private Long   id;
	private String username;
	private String password;

}
