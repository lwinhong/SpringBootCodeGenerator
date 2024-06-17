<template>
    <base-dialog ref="dialog" title='${classInfo.classComment!"标题"}' :full-screen="true" :status="form.status" @closed="$emit('closed')">
        <template #header-right>
            <el-button size="mini" :type="deleteType" :disabled="deleteDisabled" :class="{ 'btn-disabled-bg': deleteDisabled }" @click="del">删除</el-button>
            <el-button size="mini" :type="adjustType" :disabled="adjustDisabled" :class="{ 'btn-disabled-bg': adjustDisabled }" @click="adjust">调整</el-button>
            <el-button size="mini" :type="saveType" :disabled="saveDisabled" :class="{ 'btn-disabled-bg': saveDisabled }" icon="el-icon-s-claim" @click="save">保存</el-button>
            <el-button size="mini" :type="submitType" :disabled="submitDisabled" :class="{ 'btn-disabled-bg': submitDisabled }" icon="el-icon-circle-check" @click="submit">提交</el-button>
        </template>
        <layout-container>
            <layout-main>
                <base-form ref="form" :model="form" :rules="rules">
                    <base-panel title='${classInfo.classComment!"标题"}' :collapse="true">
                        <#if classInfo.fieldList?exists && classInfo.fieldList?size gt 0>
                        <#list classInfo.fieldList as fieldItem >
                        <base-form-item :span="6" label="${fieldItem.fieldComment!fieldItem.fieldName}" prop="${fieldItem.fieldName}">
                            <#if fieldItem.fieldClass =="Date">
                                <el-date-picker v-model="form.${fieldItem.fieldName}" type="date" value-format="yyyy-MM-dd" placeholder="选择日期" :disabled="!editable" />
                            <#elseif fieldItem.fieldClass =="Integer" || fieldItem.fieldClass =="Long"
                                || fieldItem.fieldClass =="Double" || fieldItem.fieldClass =="Float">
                                <el-input v-model="form.${fieldItem.fieldName}" type="number" :disabled="!editable" />
                            <#elseif fieldItem.fieldClass =="Boolean">
                                <el-radio v-model="form.${fieldItem.fieldName}" :disabled="!editable" label="01">true</el-radio>
                                <el-radio v-model="form.${fieldItem.fieldName}" :disabled="!editable" label="02">false</el-radio>
                            <#else>
                                <el-input v-model="form.${fieldItem.fieldName}" :disabled="!editable" />
                            </#if>
                        </base-form-item>
                        </#list>
                        </#if>
                    </base-panel>
                </base-form>
            </layout-main>
        </layout-container>
    </base-dialog>
</template>
<script>
<#if classInfo.fieldList?exists && classInfo.fieldList?size gt 0>
<#list classInfo.fieldList as fieldItem >
    <#if fieldItem.fieldClass =="Date">
import dayjs from 'dayjs'
        <#break>
    </#if>
</#list>
</#if>
import Utils from '@/utils/utils'
import ${classInfo.className?uncap_first} from '@/api/masterdata/${classInfo.className?uncap_first}/${classInfo.className?uncap_first}'

export default {
    name: '${classInfo.className}Detail',
    components: {},
    data() {
        return {
            loading: false,
            editable: false,
            deleteType: 'default',
            deleteDisabled: false,
            adjustType: 'default',
            adjustDisabled: false,
            saveType: 'primary',
            saveDisabled: false,
            submitType: 'primary',
            submitDisabled: false,
            addType: 'primary',
            addDisabled: false,
            operation: '',
            form: {},
            rules: {
                <#if classInfo.fieldList?exists && classInfo.fieldList?size gt 0>
                <#assign i=1>
                <#list classInfo.fieldList as fieldItem >
                <#if i gt 5>
                    <#break >
                </#if>
                ${fieldItem.fieldName}: [ { required: true, message: '${fieldItem.fieldComment}不能为空', trigger: 'blur' }],
                <#assign i+=1>
                </#list>
                </#if>
                //更多...
            }
        }
    },
    mounted() {
    },
    methods: {
        async open(row, operation) {
            this.operation = operation;
            this.row = row;
            if (operation === 'add') {
                this.editable = true;
                this.row = {};
                this.form = this.newForm();
            } else {
                this.editable = true;
            }
            this.$nextTick(() => {
                if (this.$refs.form !== null && this.$refs.form !== undefined) {
                    this.$refs.form.clearValidate()
                }
            })
            this.$refs.dialog.open()
        },
        // 设置按钮状态
        setButtonStatus() {
            const status = this.form.status

            this.editable = true
            this.deleteType = ''
            this.deleteDisabled = true
            this.adjustType = ''
            this.adjustDisabled = true
            this.saveType = 'primary'
            this.saveDisabled = false
            this.submitType = 'primary'
            this.submitDisabled = false
            this.addType = 'primary'
            this.addDisabled = false
            if (this.operation === 'add') {
                // 与初始化数据一致
            } else if (this.operation === 'edit') {
                //根据状态条件设置
                this.saveType = ''
                this.saveDisabled = true
                this.submitType = ''
                this.submitDisabled = true
                this.addType = ''
                this.addDisabled = true
                this.filedAdjustDisabled = true
            } else if (this.operation === 'show') {
                this.editable = false
                this.saveType = ''
                this.saveDisabled = true
                this.submitType = ''
                this.submitDisabled = true
                this.addType = ''
                this.addDisabled = true
            }
        },
        // 新增
        newForm() {
            return {
                id: Utils.getUUID(),
                status: '0',
                //更多...
            }
        },
        // 加载数据
        async getData() {
            const res = await ${classInfo.className?uncap_first}.findById(this.row)
            if (res.data) {
                this.form = res.data
                // 更多的逻辑在下面处理

                this.setButtonStatus()
            }
        },
        // 删除
        del() {
            this.$confirm('确认删除记录？', '提示', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning'
            }).then(() =>{
                if (this.stringUtil.isEmpty(this.form.id)) {
                    this.close()
                } else {
                    ${classInfo.className?uncap_first}.del(this.row).then(res => {
                        if (res.code === 200) {
                            this.close()
                        }
                    })
                }
            })
        },
    }
}
</script>
<style lang="scss" scoped>
    .btn-disabled-bg {
        background: #F2F3F5;
    }
</style>