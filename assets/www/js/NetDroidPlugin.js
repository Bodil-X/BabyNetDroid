/**
 * Created with IntelliJ IDEA.
 * User: DotNEt
 * Date: 12-5-7
 * Time: 下午5:42
 * To change this template use File | Settings | File Templates.
 */
var NetDroidPlugin = new function(){};
NetDroidPlugin.prototype.getConfig = function(succCallBack,failureCallBack){
    return PhoneGap.exec(succCallBack,failureCallBack,'NetDroidPlugin','getConfig',[]);
}
PhoneGap.addConstructor(function(){
    PhoneGap.addPlugin('NetDroidPlugin',new NetDroidPlugin());
});