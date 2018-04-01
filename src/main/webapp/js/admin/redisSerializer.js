$(document).ready(function() {
	
	$('#setSerializerModal').on('show.bs.modal', function(event) {
		//读取cookie中的数据
		var json = $.cookie('redis.serializer');

		$("#setModal_keySerializerKey").val("String");
		$("#setModal_valueSerializerKey").val("String");
		$("#setModal_hashKeySerializerKey").val("String");
		$("#setModal_hashValueSerializerKey").val("String");
		$("#setModal_stringSerializerKey").val("String");
		
		if(json){
			var serializer = JSON.parse(new Base64().decode(json));
			$("#setModal_enableDefaultSerializer").bootstrapSwitch('state', serializer.enableDefaultSerializer);
			$("#setModal_defaultSerializerKey").val(serializer.defaultSerializerKey);
			if(serializer.keySerializerKey) {
				$("#setModal_keySerializerKey").val(serializer.keySerializerKey);
			}
			if(serializer.valueSerializerKey) {
				$("#setModal_valueSerializerKey").val(serializer.valueSerializerKey);
			}
			if(serializer.hashKeySerializerKey) {
				$("#setModal_hashKeySerializerKey").val(serializer.hashKeySerializerKey);
			}
			if(serializer.hashValueSerializerKey) {
				$("#setModal_hashValueSerializerKey").val(serializer.hashValueSerializerKey);
			}
			if(serializer.stringSerializerKey) {
				$("#setModal_stringSerializerKey").val(serializer.stringSerializerKey);
			}
		}else{
			$("#setModal_enableDefaultSerializer").val("true");
			$("#setModal_defaultSerializerKey").val("String");
		}
		$("#setModal_enableDefaultSerializer").change();
		$("[id^=setModal_][id$=SerializerKey]").change();
	});
	
	$("#setModal_enableDefaultSerializer").on("switchChange.bootstrapSwitch", function(event, state) {
		var enable = state;
		if(enable){
			$(".serializer-detail").hide();
		}else{
			$(".serializer-detail").show();
		}
	})
	
	$("[id^=setModal_][id$=SerializerKey]").on("change", function() {
		var key = $(this).val();
		switch(key){
		case "String":
			$(this).css("color","black");
			break;
		case "Jdk":
			$(this).css("color","red");
			break;
		case "Hessian":
			$(this).css("color","blue");
			break;
		case "StringByHessian&Base64":
			$(this).css("color","blueviolet");
			break;
		case "Oxm":
			$(this).css("color","chocolate");
			break;
		}
	})
	
	$(".set_RedisSerializer_btn").on("click", function() {
		var addFormParam = $("#setSerializerForm").formSerialize();
		if(addFormParam.indexOf("enableDefaultSerializer=on")>=0){
			addFormParam = addFormParam.replace('=on','=true');
		}else{
			addFormParam = "enableDefaultSerializer=false&"+addFormParam;
		}
		
		var url = basePath + '/redis/setRedisSerializer';
		$.ajax({
			type: "post",
			url: url,
			dataType: "json",
			data: addFormParam,
			success: function(data) {
				modelAlert(data);
			}
		})
	})
		
})


/**
 * Base64 encode / decode
 * 
 * @author cuichenglong@motie.com  
 * @date 2017年11月25日 下午5:34:43
 */
function Base64() {
 
    // private property
    _keyStr = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=";
 
    // public method for encoding
    this.encode = function (input) {
        var output = "";
        var chr1, chr2, chr3, enc1, enc2, enc3, enc4;
        var i = 0;
        input = _utf8_encode(input);
        while (i < input.length) {
            chr1 = input.charCodeAt(i++);
            chr2 = input.charCodeAt(i++);
            chr3 = input.charCodeAt(i++);
            enc1 = chr1 >> 2;
            enc2 = ((chr1 & 3) << 4) | (chr2 >> 4);
            enc3 = ((chr2 & 15) << 2) | (chr3 >> 6);
            enc4 = chr3 & 63;
            if (isNaN(chr2)) {
                enc3 = enc4 = 64;
            } else if (isNaN(chr3)) {
                enc4 = 64;
            }
            output = output +
            _keyStr.charAt(enc1) + _keyStr.charAt(enc2) +
            _keyStr.charAt(enc3) + _keyStr.charAt(enc4);
        }
        return output;
    }
 
    // public method for decoding
    this.decode = function (input) {
        var output = "";
        var chr1, chr2, chr3;
        var enc1, enc2, enc3, enc4;
        var i = 0;
        input = input.replace(/[^A-Za-z0-9\+\/\=]/g, "");
        while (i < input.length) {
            enc1 = _keyStr.indexOf(input.charAt(i++));
            enc2 = _keyStr.indexOf(input.charAt(i++));
            enc3 = _keyStr.indexOf(input.charAt(i++));
            enc4 = _keyStr.indexOf(input.charAt(i++));
            chr1 = (enc1 << 2) | (enc2 >> 4);
            chr2 = ((enc2 & 15) << 4) | (enc3 >> 2);
            chr3 = ((enc3 & 3) << 6) | enc4;
            output = output + String.fromCharCode(chr1);
            if (enc3 != 64) {
                output = output + String.fromCharCode(chr2);
            }
            if (enc4 != 64) {
                output = output + String.fromCharCode(chr3);
            }
        }
        output = _utf8_decode(output);
        return output;
    }
 
    // private method for UTF-8 encoding
    _utf8_encode = function (string) {
        string = string.replace(/\r\n/g,"\n");
        var utftext = "";
        for (var n = 0; n < string.length; n++) {
            var c = string.charCodeAt(n);
            if (c < 128) {
                utftext += String.fromCharCode(c);
            } else if((c > 127) && (c < 2048)) {
                utftext += String.fromCharCode((c >> 6) | 192);
                utftext += String.fromCharCode((c & 63) | 128);
            } else {
                utftext += String.fromCharCode((c >> 12) | 224);
                utftext += String.fromCharCode(((c >> 6) & 63) | 128);
                utftext += String.fromCharCode((c & 63) | 128);
            }
 
        }
        return utftext;
    }
 
    // private method for UTF-8 decoding
    _utf8_decode = function (utftext) {
        var string = "";
        var i = 0;
        var c = c1 = c2 = 0;
        while ( i < utftext.length ) {
            c = utftext.charCodeAt(i);
            if (c < 128) {
                string += String.fromCharCode(c);
                i++;
            } else if((c > 191) && (c < 224)) {
                c2 = utftext.charCodeAt(i+1);
                string += String.fromCharCode(((c & 31) << 6) | (c2 & 63));
                i += 2;
            } else {
                c2 = utftext.charCodeAt(i+1);
                c3 = utftext.charCodeAt(i+2);
                string += String.fromCharCode(((c & 15) << 12) | ((c2 & 63) << 6) | (c3 & 63));
                i += 3;
            }
        }
        return string;
    }
}