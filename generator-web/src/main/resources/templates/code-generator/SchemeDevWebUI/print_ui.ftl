<template>
    <div id="printId" style="width: 100%; margin: 0; position: relative;display:none;">
        <h2 style="height: 30px; text-align: center; line-height: 30px; font-size: 18px; margin: 0;">
            <b>${classInfo.className}</b>
        </h2>
        <table>
            <tr>
                <td class="mt td-title" style="width:5%;">序号</td>
                <#if classInfo.fieldList?exists && classInfo.fieldList?size gt 0>
                <#list classInfo.fieldList as fieldItem >
                    <#if fieldItem.fieldName=="id">
                        <#continue >
                    </#if>
                <td class="mt td-title" style="width:15%;">${fieldItem.fieldName}</td>
                </#list>
                </#if>
            </tr>
            <tbody>
                <tr v-for="(item, index) in tableData" :key="index">
                    <td class="mt td-cell">{{ index + 1 }}</td>
                    <#if classInfo.fieldList?exists && classInfo.fieldList?size gt 0>
                    <#list classInfo.fieldList as fieldItem >
                        <#if fieldItem.fieldName=="id">
                            <#continue >
                        </#if>
                    <td class="mt td-cell">{{ item.${fieldItem.fieldName} }}</td>
                    </#list>
                    </#if>
                </tr>
            </tbody>
        </table>
    </div>
</template>
<script>
    import Print from 'print-js'
    export default {
        name: 'print${classInfo.className}',
        model: {
            prop: 'value',
            event: 'change'
        },
        props: {
            value: {
                type: Boolean,
                default: () => {
                    return false
                }
            }
        },
        data() {
            return {
                tableData: null
            }
        },
        watch: {
            billId() {
            }
        },
        mounted() {
        },
        methods: {
            printHtml(tableData) {
                this.tableData = tableData
                document.getElementById('printId').style.display = ''
                this.$nextTick(() => {
                    Print({
                        printable: 'printId',
                        type: 'html',
                        // 继承原来的所有样式
                        targetStyles: ['*']
                    })
                    document.getElementById('printId').style.display = 'none'
                })
            }
        }
    }
</script>
<style lang="scss" scoped>
    * {
        padding: 0;
        margin: 0;
        -webkit-box-sizing: border-box;
        -moz-box-sizing: border-box;
        box-sizing: border-box;
    }
    .form-horizontal .form-group[class*='col-sm']{
        margin-bottom: 0;
    }
    .form-horizontal .form-group .control-label{
        padding-top: 7px;
        color: #000;
        font-size: 13px;
    }
    .form-horizontal .form-group p{color: #000; font-size: 13px;}
    table {
        width: 90%;
        border-collapse: collapse;
        table-layout: fixed;
    }

    td {
        border: 1px solid #000;
        font-size: 13px;
        line-height: 48px;
        text-align: left;
        vertical-align: middle;
    }

    td.td-title {
        text-align: center;
        line-height:30px;
    }
    td.td-cell {
        text-align: center;
        padding-right: 5px;
    }
    td.td-cell.left {
        text-align: right;
        padding-right: 5px;
    }
    td.td-cell.right {
        text-align: right;
        padding-right: 5px;
    }
    td.qrcode {
        height: 95px;
        text-align: center;
        padding: 5px 0 0 0;
        position: relative;
    }

    td.qrcode div {
        position: absolute;
        left: 0px;
        right: 0px;
        bottom: 0px;
        line-height: 20px;
        font-size: 10px;
    }

    td.mt {
        line-height:30px;
    }
    div.text-right span{
        display: inline-block;
        margin: 10px;
        text-align: center;
        width: 86px;
    }
    div.text-right span i{display: block; width: 80px; height: 80px; margin: 0 auto;}
    div.text-right span p{display: inline-block; font-size: 12px;}
</style>

