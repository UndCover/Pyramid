#coding=utf-8
#!/usr/bin/python
class Trigger():  # 元类 默认的元类 type
    @staticmethod
    def init(sp_obj,extend=""):
        sp_obj.init(extend)
    @staticmethod
    def homeContent(sp_obj,filter):
        return sp_obj.homeContent(filter)
    @staticmethod
    def homeVideoContent(sp_obj):
        return sp_obj.homeVideoContent()
    @staticmethod
    def categoryContent(sp_obj,tid,pg,filter,extend):
        return sp_obj.categoryContent(tid,pg,filter,extend)
    @staticmethod
    def detailContent(sp_obj,ids):
        return sp_obj.detailContent(ids)
    @staticmethod
    def searchContent(sp_obj,key,quick):
        return sp_obj.searchContent(key,quick)
    @staticmethod
    def playerContent(sp_obj,flag,id,vipFlags):
        return sp_obj.playerContent(flag,id,vipFlags)
    @staticmethod
    def isVideoFormat(sp_obj,url):
        return sp_obj.isVideoFormat(url)
    @staticmethod
    def manualVideoCheck(sp_obj):
        return sp_obj.manualVideoCheck()