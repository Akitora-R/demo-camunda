<!DOCTYPE html>
<html lang="zh">
<head>
    <meta charset="UTF-8">
    <title>历史流程列表</title>
    <style>
        table, th, td {
            border: solid 1px;
        }
    </style>
</head>
<body>
<#include "common/navi.ftlh">
<h1>历史流程列表</h1>
<table>
    <thead>
    <tr>
        <th>流程id</th>
        <th>流程创建时间</th>
        <th>流程变量</th>
        <th>任务列表</th>
    </tr>
    </thead>
    <tbody>
    <#list voList as vo>
        <tr>
            <td>${vo.processInstance.id}</td>
            <td>${vo.processInstance.startTime?string("yyyy-MM-dd HH:mm:ss")}</td>
            <td>
                <#list vo.varList as var>
                    <h6>${var.name} - <#if var.value?is_boolean>${var.value?c}<#else>${var.value}</#if></h6>
                </#list>
            </td>
            <td>
                <#list vo.taskList as task>
                    <p>${task.historicTaskInstance.name}</p>
                    <#list task.varList as taskVar>
                        <h6>${taskVar.name}
                            - <#if taskVar.value?is_boolean>${taskVar.value?c}<#else>${taskVar.value}</#if></h6>
                    </#list>
                </#list>
            </td>
        </tr>
    </#list>
    </tbody>
</table>
<script>
    <#noparse>

    </#noparse>
</script>
</body>
</html>
