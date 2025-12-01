import { useState, useEffect } from "react"
import { LayoutBody } from "@/layouts/layout-definitions"
import { DataTableToolbar } from "./table/table-toolbar"
import { DataTable } from "@/components/custom/data-table/client-pagination-data-table"
import { tableColumns } from "./table/table-columns"
import { type Auth2Client, type Auth2ClientCriteriaSchema } from "@/domains/auth2-client"
import { Oauth2ClientService } from "@/services/oauth2-client-service"
import { filterTable } from "@/lib/table-utils"

export default function Auth2Client() {
  // State to hold the fetched data
  const entityName = "oauth2 client"
  const [tableData, setTableData] = useState([] as Array<Auth2Client>)

  useEffect(() => {
    loadPage()
  }, [])

  function loadPage(criteria: Auth2ClientCriteriaSchema = {}): void {
    if(criteria.keyword && tableData.length) {
      setTableData(filterTable(tableData, criteria.keyword))
      return
    }
    Oauth2ClientService.find({ page: 0, size: 2000, sort: ["updatedAt,desc"]}).then(r => {
      setTableData(r.data)
    })
  }

  function save(formData: Auth2Client): Promise<void> {
    return Oauth2ClientService.save(formData).then(() => {
      loadPage()
    })
  }

  function deleteRow(row: Auth2Client): Promise<void> {
    if(!row.id) {
      return Promise.reject("Invalid empty id")
    }
    return Oauth2ClientService.deleteById(row.id).then(() => {
      loadPage()
    })
  }

  function deleteRows(rows: Array<Auth2Client>): Promise<Array<void>> {
    return Promise.all(rows.map(deleteRow))
  }

  return (
    <LayoutBody className="flex flex-col" fixedHeight>
      <div className="-mx-4 flex-1 overflow-auto px-4 py-1 lg:flex-row lg:space-y-0">
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