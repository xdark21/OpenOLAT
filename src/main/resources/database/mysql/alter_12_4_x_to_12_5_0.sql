alter table o_loggingtable drop username, drop userproperty1, drop userproperty2, drop userproperty3, drop userproperty4, drop userproperty5, drop userproperty6, drop userproperty7, drop userproperty8, drop userproperty9, drop userproperty10, drop userproperty11, drop userproperty12;

update o_bs_identity set name=id where status=199;

update o_user set u_firstname=null from o_bs_identity where user_id=fk_user_id and status=199;
update o_user set u_lastname=null from o_bs_identity where user_id=fk_user_id and status=199;
update o_user set u_email=null from o_bs_identity where user_id=fk_user_id and status=199;
update o_user set u_birthday=null from o_bs_identity where user_id=fk_user_id and status=199;
update o_user set u_graduation=null from o_bs_identity where user_id=fk_user_id and status=199;
update o_user set u_gender=null from o_bs_identity where user_id=fk_user_id and status=199;
update o_user set u_telprivate=null from o_bs_identity where user_id=fk_user_id and status=199;
update o_user set u_telmobile=null from o_bs_identity where user_id=fk_user_id and status=199;
update o_user set u_teloffice=null from o_bs_identity where user_id=fk_user_id and status=199;
update o_user set u_skype=null from o_bs_identity where user_id=fk_user_id and status=199;
update o_user set u_msn=null from o_bs_identity where user_id=fk_user_id and status=199;
update o_user set u_xing=null from o_bs_identity where user_id=fk_user_id and status=199;
update o_user set u_icq=null from o_bs_identity where user_id=fk_user_id and status=199;
update o_user set u_homepage=null from o_bs_identity where user_id=fk_user_id and status=199;
update o_user set u_street=null from o_bs_identity where user_id=fk_user_id and status=199;
update o_user set u_extendedaddress=null from o_bs_identity where user_id=fk_user_id and status=199;
update o_user set u_pobox=null from o_bs_identity where user_id=fk_user_id and status=199;
update o_user set u_zipcode=null from o_bs_identity where user_id=fk_user_id and status=199;
update o_user set u_region=null from o_bs_identity where user_id=fk_user_id and status=199;
update o_user set u_city=null from o_bs_identity where user_id=fk_user_id and status=199;
update o_user set u_country=null from o_bs_identity where user_id=fk_user_id and status=199;
update o_user set u_countrycode=null from o_bs_identity where user_id=fk_user_id and status=199;
update o_user set u_institutionalname=null from o_bs_identity where user_id=fk_user_id and status=199;
update o_user set u_institutionaluseridentifier=null from o_bs_identity where user_id=fk_user_id and status=199;
update o_user set u_institutionalemail=null from o_bs_identity where user_id=fk_user_id and status=199;
update o_user set u_orgunit=null from o_bs_identity where user_id=fk_user_id and status=199;
update o_user set u_studysubject=null from o_bs_identity where user_id=fk_user_id and status=199;
update o_user set u_emchangekey=null from o_bs_identity where user_id=fk_user_id and status=199;
update o_user set u_emaildisabled=null from o_bs_identity where user_id=fk_user_id and status=199;
update o_user set u_typeofuser=null from o_bs_identity where user_id=fk_user_id and status=199;
update o_user set u_socialsecuritynumber=null from o_bs_identity where user_id=fk_user_id and status=199;
update o_user set u_genericselectionproperty=null from o_bs_identity where user_id=fk_user_id and status=199;
update o_user set u_genericselectionproperty2=null from o_bs_identity where user_id=fk_user_id and status=199;
update o_user set u_genericselectionproperty3=null from o_bs_identity where user_id=fk_user_id and status=199;
update o_user set u_generictextproperty=null from o_bs_identity where user_id=fk_user_id and status=199;
update o_user set u_generictextproperty2=null from o_bs_identity where user_id=fk_user_id and status=199;
update o_user set u_generictextproperty3=null from o_bs_identity where user_id=fk_user_id and status=199;
update o_user set u_generictextproperty4=null from o_bs_identity where user_id=fk_user_id and status=199;
update o_user set u_generictextproperty5=null from o_bs_identity where user_id=fk_user_id and status=199;
update o_user set u_genericuniquetextproperty=null from o_bs_identity where user_id=fk_user_id and status=199;
update o_user set u_genericuniquetextproperty2=null from o_bs_identity where user_id=fk_user_id and status=199;
update o_user set u_genericuniquetextproperty3=null from o_bs_identity where user_id=fk_user_id and status=199;
update o_user set u_genericemailproperty1=null from o_bs_identity where user_id=fk_user_id and status=199;
update o_user set u_genericcheckboxproperty=null from o_bs_identity where user_id=fk_user_id and status=199;
update o_user set u_genericcheckboxproperty2=null from o_bs_identity where user_id=fk_user_id and status=199;
update o_user set u_genericcheckboxproperty3=null from o_bs_identity where user_id=fk_user_id and status=199;
update o_user set u_rank=null from o_bs_identity where user_id=fk_user_id and status=199;
update o_user set u_degree=null from o_bs_identity where user_id=fk_user_id and status=199;
update o_user set u_position=null from o_bs_identity where user_id=fk_user_id and status=199;
update o_user set u_userinterests=null from o_bs_identity where user_id=fk_user_id and status=199;
update o_user set u_usersearchedinterests=null from o_bs_identity where user_id=fk_user_id and status=199;
update o_user set u_officestreet=null from o_bs_identity where user_id=fk_user_id and status=199;
update o_user set u_extendedofficeaddress=null from o_bs_identity where user_id=fk_user_id and status=199;
update o_user set u_officepobox=null from o_bs_identity where user_id=fk_user_id and status=199;
update o_user set u_officezipcode=null from o_bs_identity where user_id=fk_user_id and status=199;
update o_user set u_officecity=null from o_bs_identity where user_id=fk_user_id and status=199;
update o_user set u_officecountry=null from o_bs_identity where user_id=fk_user_id and status=199;
update o_user set u_officemobilephone=null from o_bs_identity where user_id=fk_user_id and status=199;
update o_user set u_department=null from o_bs_identity where user_id=fk_user_id and status=199;
update o_user set u_privateemail=null from o_bs_identity where user_id=fk_user_id and status=199;
update o_user set u_edupersonaffiliation=null from o_bs_identity where user_id=fk_user_id and status=199;
update o_user set u_swissedupersonhomeorg=null from o_bs_identity where user_id=fk_user_id and status=199;
update o_user set u_swissedupersonstudylevel=null from o_bs_identity where user_id=fk_user_id and status=199;
update o_user set u_swissedupersonhomeorgtype=null from o_bs_identity where user_id=fk_user_id and status=199;
update o_user set u_employeenumber=null from o_bs_identity where user_id=fk_user_id and status=199;
update o_user set u_swissedupersonstaffcategory=null from o_bs_identity where user_id=fk_user_id and status=199;
update o_user set u_organizationalunit=null from o_bs_identity where user_id=fk_user_id and status=199;
update o_user set u_swissedupersonstudybranch1=null from o_bs_identity where user_id=fk_user_id and status=199;
update o_user set u_swissedupersonstudybranch2=null from o_bs_identity where user_id=fk_user_id and status=199;
update o_user set u_swissedupersonstudybranch3=null from o_bs_identity where user_id=fk_user_id and status=199;
