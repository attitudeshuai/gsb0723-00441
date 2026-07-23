<script setup>
import { ref, computed, watch } from 'vue'

const props = defineProps({
  data: {
    type: Array,
    default: () => []
  },
  columns: {
    type: Array,
    default: () => []
  },
  loading: {
    type: Boolean,
    default: false
  },
  total: {
    type: Number,
    default: 0
  },
  page: {
    type: Number,
    default: 1
  },
  pageSize: {
    type: Number,
    default: 10
  },
  pageSizes: {
    type: Array,
    default: () => [10, 20, 50, 100]
  },
  showPagination: {
    type: Boolean,
    default: true
  },
  showIndex: {
    type: Boolean,
    default: false
  },
  showSelection: {
    type: Boolean,
    default: false
  },
  rowKey: {
    type: String,
    default: 'id'
  },
  border: {
    type: Boolean,
    default: true
  },
  stripe: {
    type: Boolean,
    default: true
  },
  height: {
    type: [String, Number],
    default: undefined
  },
  emptyText: {
    type: String,
    default: '暂无数据'
  }
})

const emit = defineEmits([
  'update:page',
  'update:pageSize',
  'selection-change',
  'row-click',
  'sort-change',
  'page-change'
])

const tableRef = ref(null)
const selectedRows = ref([])

const currentPage = computed({
  get: () => props.page,
  set: (val) => emit('update:page', val)
})

const currentPageSize = computed({
  get: () => props.pageSize,
  set: (val) => emit('update:pageSize', val)
})

function handleSizeChange(size) {
  currentPageSize.value = size
  currentPage.value = 1
  emit('page-change', { page: 1, pageSize: size })
}

function handleCurrentChange(page) {
  currentPage.value = page
  emit('page-change', { page, pageSize: currentPageSize.value })
}

function handleSelectionChange(selection) {
  selectedRows.value = selection
  emit('selection-change', selection)
}

function handleRowClick(row, column, event) {
  emit('row-click', row, column, event)
}

function handleSortChange({ prop, order }) {
  emit('sort-change', { prop, order })
}

function clearSelection() {
  tableRef.value?.clearSelection()
}

function toggleRowSelection(row, selected) {
  tableRef.value?.toggleRowSelection(row, selected)
}

function getSelectionRows() {
  return selectedRows.value
}

defineExpose({
  clearSelection,
  toggleRowSelection,
  getSelectionRows,
  tableRef
})
</script>

<template>
  <div class="data-table">
    <el-table
      ref="tableRef"
      v-loading="loading"
      :data="data"
      :border="border"
      :stripe="stripe"
      :height="height"
      :row-key="rowKey"
      :empty-text="emptyText"
      @selection-change="handleSelectionChange"
      @row-click="handleRowClick"
      @sort-change="handleSortChange"
    >
      <!-- 多选列 -->
      <el-table-column
        v-if="showSelection"
        type="selection"
        width="50"
        align="center"
        fixed="left"
      />

      <!-- 序号列 -->
      <el-table-column
        v-if="showIndex"
        type="index"
        label="序号"
        width="60"
        align="center"
        fixed="left"
        :index="(index) => (currentPage - 1) * currentPageSize + index + 1"
      />

      <!-- 数据列 -->
      <template v-for="col in columns" :key="col.prop || col.slot">
        <!-- 插槽列 -->
        <el-table-column
          v-if="col.slot"
          :prop="col.prop"
          :label="col.label"
          :width="col.width"
          :min-width="col.minWidth"
          :align="col.align || 'center'"
          :fixed="col.fixed"
          :sortable="col.sortable"
          :show-overflow-tooltip="col.tooltip !== false"
        >
          <template #default="scope">
            <slot :name="col.slot" v-bind="scope" />
          </template>
        </el-table-column>

        <!-- 普通列 -->
        <el-table-column
          v-else
          :prop="col.prop"
          :label="col.label"
          :width="col.width"
          :min-width="col.minWidth"
          :align="col.align || 'center'"
          :fixed="col.fixed"
          :sortable="col.sortable"
          :show-overflow-tooltip="col.tooltip !== false"
          :formatter="col.formatter"
        />
      </template>

      <!-- 操作列插槽 -->
      <slot name="action" />
    </el-table>

    <!-- 分页 -->
    <div v-if="showPagination && total > 0" class="pagination-wrapper">
      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="currentPageSize"
        :page-sizes="pageSizes"
        :total="total"
        layout="total, sizes, prev, pager, next, jumper"
        background
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>
  </div>
</template>

<style scoped lang="scss">
.data-table {
  background: #FFFFFF;
  border-radius: 20px;
  padding: 8px;
  border: 1px solid #F1F5F9;
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.05);

  :deep(.el-table) {
    --el-table-border-color: #F1F5F9;
    --el-table-header-bg-color: #FAFAFA;
    --el-table-row-hover-bg-color: rgba(0, 82, 255, 0.02);
    border-radius: 12px;
    overflow: hidden;

    th.el-table__cell {
      font-family: 'Inter', sans-serif;
      font-weight: 600;
      text-transform: uppercase;
      font-size: 11px;
      letter-spacing: 0.05em;
      color: #64748B;
      padding: 16px 0;
    }

    td.el-table__cell {
      padding: 12px 0;
      font-size: 14px;
      color: #0F172A;
    }

    .el-table__row {
      transition: all 0.3s ease;

      &:hover {
        td.el-table__cell {
          color: var(--accent);
        }
      }
    }
  }

  .pagination-wrapper {
    display: flex;
    justify-content: flex-end;
    margin-top: 20px;
    padding: 12px 8px;
    border-top: 1px solid #F1F5F9;

    :deep(.el-pagination.is-background) {
      .el-pager li:not(.is-active):hover {
        color: var(--accent);
      }
      .el-pager li.is-active {
        background: var(--signature-gradient);
        box-shadow: 0 4px 12px rgba(0, 82, 255, 0.25);
      }
    }
  }
}
</style>
