<template>
    <layout-container v-loading="loading">
        <layout-container>
            <layout-header>
                <base-toolbar :form-data="listQuery" @submit="getPage" @clear="clear">
                    <#if classInfo.fieldList?exists && classInfo.fieldList?size gt 0>
                    <#assign i=1>
                    <#list classInfo.fieldList as fieldItem >
                    <#if fieldItem.fieldName=="id">
                        <#continue >
                    </#if>
                    <#if i gt 3>
                        <#break>
                    </#if>
                    <el-form-item label="${fieldItem.fieldComment!fieldItem.fieldName}">
                        <el-input v-model="listQuery.${fieldItem.fieldName}" />
                    </el-form-item>
                    <#assign i+=1>
                    </#list>
                    </#if>

                    <template #button-group>
                        <el-button v-if="editable" type="primary" size="mini" icon="el-icon-plus" @click="openDetail(null, 'add')">新增</el-button>
                    </template>
                </base-toolbar>
            </layout-header>
            <layout-main>
                <vxe-table :data="tableData" :highlight-current-row="true" class="new-style" height="89%" size="mini"
                           resizable border stripe show-header-overflow show-overflow :row-config="{ isHover: true }" :loading="loading">
                    <vxe-column type="seq" title="序号" align="center" width="60px" />
                    <#if classInfo.fieldList?exists && classInfo.fieldList?size gt 0>
                    <#list classInfo.fieldList as fieldItem >
                    <#if fieldItem.fieldName=="id">
                        <#continue >
                    </#if>
                    <vxe-table-base-column title="${fieldItem.fieldComment!fieldItem.fieldName}"  field="${fieldItem.fieldName}" align="left" min-width="100px" show-overflow-tooltip>
                    </vxe-table-base-column >
                    </#list>
                    </#if>
                    <vxe-table-base-column v-if="editable" title="操作" align="center" show-overflow fixed="right" width="80px">
                        <template #default="{row}">
                            <table-toolbar-button v-if="row.status !== '9'" label="编辑" icon="el-icon-edit-outline" @click="openDetail(row, 'edit')" />
                            <table-toolbar-button v-if="row.status === '0'" label="删除" icon="el-icon-delete" @click="deleteRow(row)" />
                        </template>
                    </vxe-table-base-column>
                </vxe-table>

                <vxepager :total="total" :page.sync="listQuery.pageNo" :limit.sync="listQuery.pageSize" @pagination="getPage" />
            </layout-main>

            <detail ref="detail" @closed="getPage" />
        </layout-container>
    </layout-container>
</template>
<script>
import ${classInfo.className?uncap_first} from '@/api/masterdata/${classInfo.className?uncap_first}/${classInfo.className?uncap_first}'
const OrgSelect = () => import('@/components/sys/Organization/org-select')
const Detail = () => import('./detail')
export default {
    name: '${classInfo.className}',
    components: {
        Detail,
    },
    data() {
        return {
            editable: false,
            loading: false,
            tableData: [],
            total: 0,
            localStorage: localStorage,
            listQuery: {
                pageNo: 1,
                pageSize: 20,
                <#if classInfo.fieldList?exists && classInfo.fieldList?size gt 0>
                <#assign i=1>
                <#list classInfo.fieldList as fieldItem >
                <#if fieldItem.fieldName=="id">
                    <#continue >
                </#if>
                <#if i gt 3>
                <#break>
                </#if>
                ${fieldItem.fieldName}
                <#assign i+=1>
                </#list>
                </#if>
            }
        }
    },
    mounted() {
        this.baseUtil.hasPermission('masterdata_xxx_edit').then(res => {
            this.editable = res.data
        })
        this.getPage()
    },
    methods: {
        // 获取列表数据
        getPage() {
            this.loading = true
            ${classInfo.className?uncap_first}.page(this.listQuery).then(res => {
                if (res.code === 200) {
                    this.tableData = res.data.recordList
                    this.total = res.data.totalRecord
                }
            })
            this.loading = false
        },
        // 显示详情（operation可为add新增,edit修改,show显示,新增时row为null）
        openDetail(row, operation) {
            this.$refs.detail.open(row, operation)
        },
        // 删除
        deleteRow(row) {
            this.$confirm('确认删除记录？', '提示', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning'
            }).then(() => {
                ${classInfo.className?uncap_first}.del(row).then(res => {
                    if (res.code === 200) {
                        this.getPage()
                    }
                })
            })
        },
        handlePageChange({ currentPage, pageSize }) {
            this.listQuery.pageNo = currentPage
            this.listQuery.pageSize = pageSize
            this.getPage()
        },
        clear() {
            <#if classInfo.fieldList?exists && classInfo.fieldList?size gt 0>
            <#assign i=1>
            <#list classInfo.fieldList as fieldItem >
            <#if fieldItem.fieldName=="id">
            <#continue >
            </#if>
            <#if i gt 3>
            <#break>
            </#if>
            this.listQuery.${fieldItem.fieldName} = ''
            <#assign i+=1>
            </#list>
            </#if>
        }
    }
}
</script>