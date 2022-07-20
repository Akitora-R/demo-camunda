# event listen example

1. [`executionListener`](https://docs.camunda.org/manual/7.9/user-guide/process-engine/delegation-code/#execution-listener)
2. [`service task`](https://docs.camunda.org/get-started/spring/service-task/#invoke-a-spring-bean-from-a-bpmn-2-0-service-task)
3. [`spring 事件机制`](https://docs.camunda.org/manual/7.16/user-guide/spring-boot-integration/the-spring-event-bridge/)

# input/output variable

对所有有可选分支的user task使用统一变量，输出不同的变量到各自的分支组。
[`流程变量最佳实践`](https://docs.camunda.io/docs/components/best-practices/development/handling-data-in-processes/)

考虑可以用[`EL表达式语法`](https://docs.camunda.org/manual/7.17/user-guide/process-engine/expression-language/)，
或是[`事件监听方式`](#event-listen-example)中的`2`。

# json process definition example

```json
{
  "procDefName": "流程",
  "procDefCode": "A",
  "nodeList": [
    {
      "id": "task_0",
      "code": "a_0a",
      "label": "主管审核",
      "assignee": "${chargerAssignee}",
      "shape": "TASK"
    },
    {
      "id": "task_1",
      "code": "a_0a2",
      "label": "经理审核",
      "assignee": "${managerAssignee}",
      "shape": "TASK"
    },
    {
      "id": "endEvent_0",
      "label": "结束",
      "shape": "END_EVENT"
    },
    {
      "id": "startEvent_0",
      "label": "开始",
      "shape": "START_EVENT"
    },
    {
      "shape": "EDGE",
      "source": "startEvent_0",
      "target": "task_0"
    },
    {
      "shape": "EDGE",
      "source": "task_0",
      "target": "task_1"
    },
    {
      "shape": "EDGE",
      "source": "task_1",
      "target": "endEvent_0"
    }
  ],
  "formDefDTO": {
    "formDef": {
      "title": "表单标题"
    },
    "formItemDTOList": [
      {
        "formItem": {
          "formItemLabel": "文字输入",
          "formItemKey": "text_0",
          "disabled": false,
          "formItemType": "TEXT_INPUT"
        },
        "formItemPropList": [
          {
            "propKey": "max_len",
            "propVal": "100"
          }
        ]
      },
      {
        "formItem": {
          "formItemLabel": "下拉选择",
          "formItemKey": "sel_0",
          "disabled": false,
          "formItemType": "SELECT"
        },
        "formItemPropList": [
          {
            "propKey": "item",
            "children": [
              {
                "propKey": "label",
                "propVal": "选项1"
              },
              {
                "propKey": "value",
                "propVal": "opt_1"
              }
            ]
          },
          {
            "propKey": "item",
            "children": [
              {
                "propKey": "label",
                "propVal": "选项2"
              },
              {
                "propKey": "value",
                "propVal": "opt_2"
              }
            ]
          }
        ]
      }
    ]
  }
}
```
