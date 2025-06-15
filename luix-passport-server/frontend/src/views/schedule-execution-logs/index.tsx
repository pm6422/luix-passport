import { useState } from "react"
import { LayoutBody } from "@/layouts/layout-definitions"
import { DataTableToolbar } from "./table/table-toolbar"
import { DataTable } from "@/components/custom/data-table/server-pagination-data-table"
import { tableColumns } from "./table/table-columns"
import { type ScheduleExecutionLog, type ScheduleExecutionLogCriteriaSchema } from "@/domains/schedule-execution-log"
import { ScheduleExecutionLogService } from "@/services/schedule-execution-log-service"

export default function ScheduleExecutionLogs() {
  // State to hold the fetched data
  const entityName = "schedule execution log"
  const [tableData, setTableData] = useState([] as Array<ScheduleExecutionLog>)
  const [totalCount, setTotalCount] = useState(0)
  const [totalPages, setTotalPages] = useState(0)

  function loadPage(pageNo: number = 0, pageSize: number = 10, sorts: Array<string> = ["startAt,desc"], criteria: ScheduleExecutionLogCriteriaSchema = {}): void {
    ScheduleExecutionLogService.find({
      page: pageNo,
      size: pageSize,
      sort: sorts,
      scheduleName: criteria.scheduleName || null,
      status: criteria.status || null
    }).then(r => {
      setTableData(r.data)
      const total = parseInt(r.headers["x-total-count"])
      setTotalCount(total)
      setTotalPages(Math.ceil(total / pageSize))
    })
  }

  function deleteRow(row: ScheduleExecutionLog): Promise<void> {
    if(!row.id) {
      return Promise.reject("Invalid empty id")
    }
    return ScheduleExecutionLogService.deleteById(row.id).then(() => {
      loadPage()
    })
  }

  function deleteRows(rows: Array<ScheduleExecutionLog>): Promise<Array<void>> {
    return Promise.all(rows.map(deleteRow))
  }

  return (
    <LayoutBody className="flex flex-col" fixedHeight>
      <div className="-mx-4 flex-1 overflow-auto px-4 py-1 lg:flex-row lg:space-x-12 lg:space-y-0">
        <DataTable 
          columns={tableColumns(entityName, deleteRow)}
          data={tableData} 
          totalCount={totalCount} 
          totalPages={totalPages} 
          loadPage={loadPage} 
          deleteRows={deleteRows}
        >
          <DataTableToolbar loadPage={loadPage} />
        </DataTable>
      </div>
    </LayoutBody>
  )
}