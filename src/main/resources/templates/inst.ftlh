<!DOCTYPE html>
<html lang="zh">
<head>
    <meta charset="UTF-8">
    <title>流程实例列表</title>
</head>
<body>
<#include "common/navi.ftlh">
<h1>流程实例列表</h1>
<#list processInstanceList as pi>
    <p>${pi.id} - ${pi.businessKey}</p>
</#list>
</body>
</html>
