<!DOCTYPE html>
<html lang="zh">
<head>
    <meta charset="UTF-8">
    <title>流程实例列表</title>
    <style>
        table, th, td {
            border: solid 1px;
        }
    </style>
</head>
<body>
<#include "common/navi.ftlh">
<h1>流程实例列表</h1>
<table>
    <thead>
    <tr>
        <th>流程实例id</th>
        <th>流程实例bk</th>
        <th>操作</th>
    </tr>
    </thead>
    <tbody>
    <#list processInstanceList as pi>
        <tr>
            <td>${pi.id}</td>
            <td>${pi.businessKey}</td>
            <td>
                <button onclick="delProcInst(this,'${pi.id}')">删除</button>
            </td>
        </tr>
    </#list>
    </tbody>
</table>
<script>
    <#noparse>
    const delProcInst = (btn, id) => {
        btn.disabled = true;
        fetch(`/inst/del/${id}`, {
            method: "DELETE"
        }).then(() => alert("删除成功")).catch(() => alert("发生错误")).finally(() => location.reload());
    }
    </#noparse>
</script>
</body>
</html>
