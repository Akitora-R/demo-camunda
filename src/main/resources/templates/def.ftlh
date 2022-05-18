<!DOCTYPE html>
<html lang="zh">
<head>
    <meta charset="UTF-8">
    <title>流程定义</title>
    <style>
        table, th, td {
            border: solid 1px;
        }
    </style>
</head>
<body>
<#include "common/navi.ftlh">
<h1>流程定义列表</h1>
<table>
    <thead>
    <tr>
        <th>流程定义id</th>
        <th>流程定义key</th>
        <th>流程定义name</th>
        <th>操作</th>
    </tr>
    </thead>
    <tbody>
    <#list pdList as pd>
        <tr>
            <td>${pd.id}</td>
            <td>${pd.key}</td>
            <td>${pd.name}</td>
            <td>
                <button onclick="startProc(this,'${pd.id}')">开始流程</button>
            </td>
        </tr>
    </#list>
    </tbody>
</table>
<#noparse>
    <script>
        function startProc(btn, procId) {
            btn.disabled = true;
            fetch(`/inst/start/${procId}`).then(r => r.json()).then(r => window.location.href = "/inst").catch(e => alert(e))
                .finally(() => btn.disabled = false);
        }
    </script>
</#noparse>
</body>
</html>
