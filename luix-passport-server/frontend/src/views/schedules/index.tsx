import { useState } from "react"
import { LayoutBody } from "@/layouts/layout-definitions"
import { DataTableToolbar } from "./table/table-toolbar"
import { DataTable } from "@/components/custom/data-table/server-pagination-data-table"
import { tableColumns } from "./table/table-columns"
import { type Schedule, type ScheduleCriteriaSchema } from "@/domains/schedule"
import { ScheduleService } from "@/services/schedule-service"

export default function Schedules() {
  // State to hold the fetched data
  const entityName = "schedule"
  const [tableData, setTableData] = useState([] as Array<Schedule>)
  const [totalCount, setTotalCount] = useState(0)
  const [totalPages, setTotalPages] = useState(0)

  function loadPage(pageNo: number = 0, pageSize: number = 10, sorts: Array<string> = ["lockedAt,desc"], criteria: ScheduleCriteriaSchema = {}): void {
    ScheduleService.find({
      page: pageNo,
      size: pageSize,
      sort: sorts,
      id: criteria.id || null
    }).then(r => {
      setTableData(r.data)
      const total = parseInt(r.headers["x-total-count"])
      setTotalCount(total)
      setTotalPages(Math.ceil(total / pageSize))
    })
  }

  function deleteRow(row: Schedule): Promise<void> {
    if(!row.id) {
      return Promise.reject("Invalid empty id")
    }
    return ScheduleService.deleteById(row.id).then(() => {
      loadPage()
    })
  }

  function deleteRows(rows: Array<Schedule>): Promise<Array<void>> {
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