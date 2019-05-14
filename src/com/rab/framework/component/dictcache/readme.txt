==================================
          代码表缓存管理
==================================


部署配置:
----------------------------------
1. sword.xml
............................
//是否将代码表数据缓存到内存.
//如果不缓存到内存,则访问时直接从数据库中获取数据,一般用于开发调试
<config name="use-memory-cache" value="true" />

//缓存更新周期,单位为分钟
<config name="update-cyc" value="1000" />

//缓存主表名称
<config name="catalog-table" value="t_xt_hcbxx" />

//
<config name="is-server" value="true" />

//缓存数据库对应的数据源,
//如果使用的是默认数据源,则需要删除如下配置信息
//如果使用了非默认数据源,则需要正确设置如下配置,配置值为 domain-core.xml中交易cachehandler对应的数据源.
<config name="dao-resource-name" value="odsDAO" />


2. domain-core.xml
............................
		<transaction name="cachehandler"> 
			<action-list>
				<proxy-ref name="cachehandlerProxy"/> 
			</action-list>
			<resource-list>
				//如下配置项可以根据需要调整
				<resource-ref name="odsDAO"/>
			</resource-list>
		</transaction>

create table T_XT_HCBXX
(
  BM_MC     VARCHAR(20) not null,
  GX_XH     decimal(16,0),
  CACHETYPE decimal(16,0),
  BM_MS     VARCHAR(20),
  ORDERBY   VARCHAR(30),
  DESCBJ    decimal(16,0),
  CODENAME  VARCHAR(20) not null,
  VALUENAME VARCHAR(20) not null,
  PRIMARY KEY (BM_MC)
); 


delete from T_XT_HCBXX;
insert into T_XT_HCBXX
select * from (
select 
	table_name 	as bm_mc, 
	0 			as gx_xh, 
	0 			as CACHETYPE, 
	table_name 	as bm_ms,
	'' 			as ORDERBY,
	0 			as DESCBJ,
	'code'		as CODENAME,
	'value' 	as VALUENAME
	from tabs where table_name like 'DICT%' and num_rows > 0 and num_rows <= 1000

UNION

select 
	table_name 	as bm_mc, 
	0 			as gx_xh, 
	1 			as CACHETYPE, 
	table_name 	as bm_ms,
	'' 			as ORDERBY,
	0 			as DESCBJ,
	'code'		as CODENAME,
	'value' 	as VALUENAME
	from tabs where table_name like 'DICT%' and num_rows > 1000

);

COMMIT;
 
 