import { useState, useEffect } from "react"
import { LayoutBody } from "@/layouts/layout-definitions"
import { DataTableToolbar } from "./table/table-toolbar"
import { DataTable } from "@/components/custom/data-table/client-pagination-data-table"
import { tableColumns } from "./table/table-columns"
import { type Role, type RoleCriteriaSchema } from "@/domains/role"
import { RoleService } from "@/services/role-service"
import { filterTable } from "@/lib/utils"

export default function Role() {
  // State to hold the fetched data
  const entityName = "role"
  const [tableData, setTableData] = useState([] as Array<Role>)

  useEffect(() => {
    loadPage()
  }, [])

  function loadPage(criteria: RoleCriteriaSchema = {}): void {
    if(criteria.keyword && tableData.length) {
      setTableData(filterTable(tableData, criteria.keyword))
      return
    }
    RoleService.find({ page: 0, size: 2000, sort: ["modifiedAt,desc"]}).then(r => {
      setTableData(r.data)
    })
  }

  function save(formData: Role): Promise<void> {
    return RoleService.save(formData).then(() => {
      loadPage()
    })
  }

  function deleteRow(row: Role): Promise<void> {
    if(!row.id) {
      return Promise.reject("Invalid empty id")
    }
    return RoleService.deleteById(row.id).then(() => {
      loadPage()
    })
  }

  function deleteRows(rows: Array<Role>): Promise<Array<void>> {
    return Promise.all(rows.map(deleteRow))
  }

  return (
    <LayoutBody className="flex flex-col" fixedHeight>
      <div className="-mx-4 flex-1 overflow-auto px-4 py-1 lg:flex-row lg:space-x-12 lg:space-y-0">
        <DataTable 
          columns={tableColumns(entityName, save, deleteRow)} 
          data={tableData} 
          loadPage={loadPage} 
          deleteRows={deleteRows}
        >
          <DataTableToolbar entityName={entityName} loadPage={loadPage} save={save} />
        </DataTable>
      </div>
    </LayoutBody>
  )
}