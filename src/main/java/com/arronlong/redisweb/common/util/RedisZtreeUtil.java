package com.arronlong.redisweb.common.util;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

import com.arronlong.redisweb.common.ztree.RedisAttach;
import com.arronlong.redisweb.common.ztree.ZLeaf;
import com.arronlong.redisweb.common.ztree.ZNode;

/**
 * Redis-Ztree工具类
 * 
 * @author cuichenglong@motie.com  
 * @date 2017年11月25日 上午11:37:07
 */
public class RedisZtreeUtil implements Constant{

	/**
	 * 初始化Ztree结构（基于redis链接名称和所有db中的所有key）
	 * 
	 * @param serverName
	 */
	public static void initRedisNavigateZtree(String serverName) {
		for(int i=0;i<=REDIS_DEFAULT_DB_SIZE;i++) {
			initRedisNavigateZtree(serverName, i);
		}
		
	}

	/**
	 * 初始化Ztree结构（基于redis链接名称和指定db中的所有key）
	 * 
	 * @param serverName
	 * @param dbIndex
	 * @return
	 */
	private static ZNode initRedisNavigateZtree(String serverName, int dbIndex) {
		ZNode serverZnode = null;
		serverZnode = ZNode.makeZNode(serverName, new RedisAttach(serverName));
		int serverZnodeIndex = redisNavigateZtree.indexOf(serverZnode);
		if(serverZnodeIndex<0) {
			redisNavigateZtree.add(serverZnode);
		} else {
			serverZnode = redisNavigateZtree.get(serverZnodeIndex);
		}
		
		ZNode dbIndexZnode = getRedisNavigateZtree(serverName, dbIndex);
		serverZnode.addChildren(dbIndexZnode);
		return serverZnode;
	}
	
	/**
	 * 刷新Ztree结构（基于redis链接名称和所有key）
	 * 
	 * @param serverName
	 */
	public static void refreshRedisNavigateZtree(String serverName) {
		ZNode serverZnode = ZNode.makeZNode(serverName, new RedisAttach(serverName));
		serverZnode.resetChildren();
		int serverZnodeIndex = redisNavigateZtree.indexOf(serverZnode);
		if(serverZnodeIndex>=0) {
			redisNavigateZtree.remove(serverZnodeIndex);
		}
		redisNavigateZtree.add(serverZnode);
		for(int i=0;i<=REDIS_DEFAULT_DB_SIZE;i++) {
			refreshRedisNavigateZtree(serverZnode, serverName, i);
		}
	}
	
	/**
	 * 刷新Ztree结构（基于redis链接名称和所有key）
	 * 
	 * @param serverZnode
	 * @param serverName
	 * @param dbIndex
	 * @return
	 */
	private static ZNode refreshRedisNavigateZtree(ZNode serverZnode, String serverName, int dbIndex) {
		ZNode dbIndexZnode = getRedisNavigateZtree(serverName, dbIndex);
		serverZnode.addChildren(dbIndexZnode);
		return serverZnode;
	}

	/**
	 * 获取Ztree结构（基于redis链接名称和所有key）
	 * 
	 * @param serverName
	 * @param dbIndex
	 * @return
	 */
	protected static ZNode getRedisNavigateZtree(String serverName, int dbIndex) {
		ZNode dbIndexZnode = ZNode.makeZNode(dbIndex+"", new RedisAttach(serverName, dbIndex));
		CopyOnWriteArrayList<RKey> redisKeysList = redisKeysListMap.get(serverName+DEFAULT_SEPARATOR+dbIndex);
		if(redisKeysList==null||redisKeysList.size()==0) {
			return null;
		}
		for(RKey rkey: redisKeysList) {
			if(rkey.contains(DEFAULT_REDISKEY_SEPARATOR)) {
				String[] prefixs = rkey.split(DEFAULT_REDISKEY_SEPARATOR);
				findParentAndSetChild(dbIndexZnode, serverName, dbIndex, prefixs, 0);
			}
		}
		return dbIndexZnode;
	}

	private static void findParentAndSetChild(ZNode parentNode, String serverName, int dbIndex, String[] prefixs, int depth) {
		if(depth >= prefixs.length-1) return;
		ZNode newChild = ZNode.makeZNode(prefixs[depth], null);
		if(!parentNode.getChildren().contains(newChild)) {
			String[] prefix = new String[depth+1];
			System.arraycopy(prefixs, 0, prefix, 0, depth+1);;
			ZNode child = ZNode.makeZNode(prefixs[depth], new RedisAttach(serverName, dbIndex, prefix, depth));
			parentNode.addChildren(child);
			findParentAndSetChild(child, serverName, dbIndex, prefixs, depth+1);
		} else {
			Set<ZLeaf> children = parentNode.getChildren();
			for(ZLeaf child : children) {
				if(child.equals(newChild)) {
					findParentAndSetChild((ZNode)child, serverName, dbIndex, prefixs, depth+1);
				}
			}
		}
	}
}
