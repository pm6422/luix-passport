import { useState, useCallback } from "react"
import { LayoutBody } from "@/layouts/layout-definitions"
import { DataTableToolbar } from "./table/schedule-log-table-toolbar"
import { PaginatedDataTable } from "@/components/custom/data-table/paginated-data-table"
import { tableColumns } from "./table/schedule-log-table-columns"
import { type ScheduleLog, type ScheduleLogCriteriaSchema } from "@/domains/schedule-log"
import { ScheduleLogService } from "@/services/schedule-log-service"
import { RoleDeveloper } from "@/layouts/permission/role-control"

export default function ScheduleLogs() {
  const entityName = "schedule log"
  const [tableData, setTableData] = useState([] as Array<ScheduleLog>)
  const [totalCount, setTotalCount] = useState(0)
  const [totalPages, setTotalPages] = useState(0)

  const loadPage = useCallback((
    pageNo: number = 0,
    pageSize: number = 10,
    sorts: Array<string> = ["startAt,desc"],
    criteria: ScheduleLogCriteriaSchema = {}
  ): void => {
    ScheduleLogService.find({
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
  }, [])

  function deleteRow(row: ScheduleLog): Promise<void> {
    if (!row.id) {
      return Promise.reject("Invalid empty id")
    }
    return ScheduleLogService.deleteById(row.id).then(() => {
      loadPage()
    })
  }

  function deleteRows(rows: Array<ScheduleLog>): Promise<Array<void>> {
    return Promise.all(rows.map(deleteRow))
  }

  return (
    <RoleDeveloper>
      <LayoutBody className="flex flex-col" fixedHeight>
        <div className="-mx-4 flex-1 overflow-auto px-4 py-1 lg:flex-row lg:gap-y-0">
          <PaginatedDataTable
            paginationMode="server"
            columns={tableColumns(entityName, deleteRow)}
            data={tableData}
            totalCount={totalCount}
            totalPages={totalPages}
            loadPage={loadPage}
            deleteRows={deleteRows}
          >
            <DataTableToolbar loadPage={loadPage} />
          </PaginatedDataTable>
        </div>
      </LayoutBody>
    </RoleDeveloper>
  )
}
