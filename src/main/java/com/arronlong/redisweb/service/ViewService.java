package com.arronlong.redisweb.service;

import java.util.Set;

import com.arronlong.redisweb.common.util.Pagination;
import com.arronlong.redisweb.common.util.RKey;
import com.arronlong.redisweb.common.ztree.ZNode;

public interface ViewService {

	Set<ZNode> getLeftTree();

	Set<RKey> getRedisKeys(Pagination pagination, String serverName, String dbIndex,
			String[] keyPrefixs, String queryKey, String queryValue);

	Set<ZNode> refresh();

	void changeRefreshMode(String mode);

	void changeShowType(String state);

	void refreshAllKeys();

}
