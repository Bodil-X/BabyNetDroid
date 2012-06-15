/**
 * Created with IntelliJ IDEA.
 * User: DotNEt
 * Date: 12-5-7
 * Time: 下午5:42
 * To change this template use File | Settings | File Templates.
 */
var NetDroidPlugin = function(){};
NetDroidPlugin.prototype.getConfig = function(succCallBack,failureCallBack){
    return cordova.exec(succCallBack,failureCallBack,'NetDroidPlugin','getConfig',[]);
}
NetDroidPlugin.prototype.getSSIDs = function(succCallBack,failureCallBack){
    return cordova.exec(succCallBack,failureCallBack,'NetDroidPlugin','getSSIDs',[]);
}
cordova.addConstructor(function(){
    cordova.addPlugin('NetDroidPlugin',new NetDroidPlugin());
});