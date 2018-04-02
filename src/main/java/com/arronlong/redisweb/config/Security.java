/**
 * Copyright 2006-2015 handu.com
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.arronlong.redisweb.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
@EnableWebSecurity
public class Security extends WebSecurityConfigurerAdapter {

    @Autowired
    Environment env;

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication()
                .withUser(env.getProperty("manager.username", "root"))
                .password(env.getProperty("manager.password", "password"))
                .roles("MANAGER");
        
        auth.inMemoryAuthentication()
        .withUser(env.getProperty("guest.username", "root"))
        .password(env.getProperty("guest.password", "password"))
        .roles("GUEST");
    }

	protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()//该方法所返回的对象的方法来配置请求级别的安全细节
//                .anyRequest().permitAll()
        		//定义路径保护的配置方法
                .antMatchers("/*/update*", "/*/del*").hasRole("MANAGER")
                .antMatchers(HttpMethod.POST, "/redis/KV").hasRole("MANAGER")
		        .antMatchers(HttpMethod.OPTIONS).permitAll() //忽略所有的options方法
                .anyRequest().authenticated()
                .and().httpBasic();
        http.csrf().disable();//禁用CSRF
        http.headers().frameOptions().disable();//关闭iframe限制
    }
}