<!DOCTYPE html>
<html lang="zh">
<head>
    <meta charset="UTF-8">
    <title>流程定义</title>
</head>
<body>
<#include "common/navi.ftlh">
<h1>流程定义列表</h1>
<#list pdList as pd>
    <p>${pd.id} - ${pd.key} - ${pd.name}
        <button onclick="startProc(this,'${pd.id}')">开始流程</button>
    </p>
</#list>
<#noparse>
    <script>
        function startProc(btn, procId) {
            btn.disabled = true;
            fetch(`/inst/start/${procId}`).then(r => r.json()).then(r => console.log(r)).catch(e => alert(e))
                .finally(() => btn.disabled = false);
        }
    </script>
</#noparse>
</body>
</html>
