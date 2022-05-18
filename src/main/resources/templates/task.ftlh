<!DOCTYPE html>
<html lang="zh">
<head>
    <meta charset="UTF-8">
    <title>任务实例列表</title>
    <style>
        table, th, td {
            border: solid 1px;
        }
    </style>
</head>
<body>
<#include "common/navi.ftlh">
<h1>任务实例列表</h1>
<table>
    <thead>
    <tr>
        <th>任务id</th>
        <th>任务name</th>
        <th>任务创建时间</th>
        <th>任务分配给</th>
        <th>操作</th>
    </tr>
    </thead>
    <tbody>
    <#list taskList as task>
        <tr>
            <td>${task.id}</td>
            <td>${task.name}</td>
            <td>${task.createTime?string("yyyy-MM-dd HH:mm:ss")}</td>
            <td>${(task.assignee)!"无"}</td>
            <td>
                <button onclick="approveTask(this,'${task.id}')">同意</button>
                <button onclick="rejectTask(this,'${task.id}')">拒绝</button>
            </td>
        </tr>
    </#list>
    </tbody>
</table>
<script>
    <#noparse>
    const approveTask = (btn, id) => req(btn, id, true);
    const rejectTask = (btn, id) => req(btn, id, false);
    const req = (btn, id, result) => {
        btn.disabled = true;
        fetch(`/task/complete/${id}?approval=${result}`).then(r => r.json()).catch(() => alert("发生错误")).finally(() => location.reload());
    }
    </#noparse>
</script>
</body>
</html>
