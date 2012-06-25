/**
 * Created with IntelliJ IDEA.
 * User: Bodil
 * Date: 12-6-25
 * Time: 下午4:33
 */
(function ($, undefined) {
    function getDb() {
        return window.openDatabase('NetDroid', '1.0', 'NetDroid', 1000000);
    }
    var _db;
    $.netDroidDB = function () { };
    $.extend($.netDroidDB, {
        db:_db ? _db : (_db = getDb()),
        createRuleTable:function (createSucc) {
            var db = this.db;
            db.transaction(function (tx) {
                var createTableSql = 'create table if not exists NetDroidRules(id integer primary key AUTOINCREMENT,' +
                    'ssidName varchar(150) not null,ip varchar(100) not null,' +
                    'netmask varchar(100) not null,gateway varchar(100) not null,dns1 varchar(100) not null,' +
                    'dns2 varchar(100) not null,sortOrder integer AUTO_INCREMENT,' +
                    "lastModified datetime default (datetime('now','localtime')),status integer default 1);";
                tx.executeSql('drop table if exists NetDroidRules;');
                tx.executeSql(createTableSql);
                tx.executeSql('create unique index NetDroidRules_idx on NetDroidRules(ssidName);');
            }, function (err) {
                alert("Error processing SQL: " + err.code + ' message:' + err.message);
            }, createSucc);
        }
    });

}(jQuery, undefined));