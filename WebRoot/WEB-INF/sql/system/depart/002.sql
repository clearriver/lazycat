T.SYSUSER_ACCOUNT,T.SYSUSER_NAME,T.SYSUSER_MOBILE,C.COMPANY_NAME 
FROM PLAT_SYSTEM_SYSUSER T LEFT JOIN PLAT_SYSTEM_COMPANY C
 ON C.COMPANY_ID=T.SYSUSER_COMPANYID
  WHERE T.SYSUSER_ID IN 