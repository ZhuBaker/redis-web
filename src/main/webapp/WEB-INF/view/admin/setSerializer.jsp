<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
    String basePath = request.getContextPath();
%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<div class="modal fade bs-example-modal-lg" id="setSerializerModal" tabindex="-1" role="dialog" aria-labelledby="setSerializerModalLabel">
	<div class="modal-dialog modal-lg" role="document">
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="back_btn close" data-dismiss="modal" aria-label="Close">
					<span aria-hidden="true">&times;</span>
				</button>
				<h4 class="modal-title" id="setSerializerModalLabel">view/update redisSerializer</h4>
			</div>
			<div class="modal-body">
	            <form id="setSerializerForm" value1="setSerializerForm">
					
					<div class="form-group">
                        <label for="setModal_enableDefaultSerializer" class="control-label">enableDefaultSerializer:</label>
					    <input id="setModal_enableDefaultSerializer" name="enableDefaultSerializer" type="checkbox" checked
					     data-on-text="Yes" data-off-text="No" data-on-color="primary" data-off-color="success"/>
						<!-- <select id="setModal_enableDefaultSerializer" name="enableDefaultSerializer" class="form-control">
							<option checked=true value="true">true</option>
							<option value="false">false</option>
						</select> -->
					</div>
					
					<div class="form-group input-group">
                        <label for="setModal_defaultSerializerKey" class="control-label">defaultSerializer：</label> 
                        <select id="setModal_defaultSerializerKey" name="defaultSerializerKey" class="form-control" >
                            <c:forEach items="${redisSerializers }" var="redisSerializer">
                                <option value="${redisSerializer.key }">【${redisSerializer.key }】- ${redisSerializer.value }</option>
                            </c:forEach>
                        </select>
                    </div>
					
					<div class="form-group serializer-detail" style="display: none;">
                        <label for="setModal_keySerializerKey" class="control-label">keySerializer：</label> 
                        <select id="setModal_keySerializerKey" name="keySerializerKey" class="form-control" >
                            <c:forEach items="${redisSerializers }" var="redisSerializer">
                                <option value="${redisSerializer.key }">【${redisSerializer.key }】- ${redisSerializer.value }</option>
                            </c:forEach>
                        </select>
                    </div>
					
					<div class="form-group serializer-detail" style="display: none;">
                        <label for="setModal_valueSerializerKey" class="control-label">valueSerializer：</label> 
                        <select id="setModal_valueSerializerKey" name="valueSerializerKey" class="form-control" >
							<c:forEach items="${redisSerializers }" var="redisSerializer">
                                <option value="${redisSerializer.key }">【${redisSerializer.key }】- ${redisSerializer.value }</option>
                            </c:forEach>
                        </select>
                    </div>
					
					<div class="form-group serializer-detail" style="display: none;">
                        <label for="recipient-name" class="control-label">hashKeySerializer：</label> 
                        <select id="setModal_hashKeySerializerKey" name="hashKeySerializerKey" class="form-control">
							<c:forEach items="${redisSerializers }" var="redisSerializer">
                                <option value="${redisSerializer.key }">【${redisSerializer.key }】- ${redisSerializer.value }</option>
                            </c:forEach>
                        </select>
                    </div>
					
					<div class="form-group serializer-detail" style="display: none;">
                        <label for="recipient-name" class="control-label">hashValueSerializer：</label> 
                        <select id="setModal_hashValueSerializerKey" name="hashValueSerializerKey" class="form-control">
							<c:forEach items="${redisSerializers }" var="redisSerializer">
                                <option value="${redisSerializer.key }">【${redisSerializer.key }】- ${redisSerializer.value }</option>
                            </c:forEach>
                        </select>
                    </div>
					
					<div class="form-group serializer-detail" style="display: none;">
                        <label for="recipient-name" class="control-label">stringSerializer：</label> 
                        <select id="setModal_stringSerializerKey" name="stringSerializerKey" class="form-control">
							<c:forEach items="${redisSerializers }" var="redisSerializer">
                                <option value="${redisSerializer.key }">【${redisSerializer.key }】- ${redisSerializer.value }</option>
                            </c:forEach>
                        </select>
                    </div>
				</form>
			</div>
			
			<div class="modal-footer">
				<button type="button" class="close_btn btn btn-default" data-dismiss="modal">close</button>
				<button type="button" class="set_RedisSerializer_btn btn btn-info">set</button>
			</div>
		</div>
	</div>
</div>