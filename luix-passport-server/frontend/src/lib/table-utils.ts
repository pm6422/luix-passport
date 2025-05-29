import { ColumnSort } from "@tanstack/react-table"
import { isBoolean, isString, isArray, cloneDeep, toLower } from "lodash"

export function parseSorts(sorting: Array<ColumnSort>): Array<string> | undefined {
  return sorting?.length
    ? sorting.map(({ id, desc }) => `${id},${desc ? "desc" : "asc"}`)
    : undefined
}

export function filterTable(initialTableData: Array<any>, searchKeyword: string): Array<never> {
  const tableData = cloneDeep(initialTableData)
  let results: Array<any> = []
  for (let i = 0; i < tableData.length; i++) {
    if (searchRow(tableData[i], searchKeyword)) {
      results.push(tableData[i])
    }
  }
  // @ts-ignore
  return cloneDeep(results)
}

function searchRow(row: any, keyword: string): boolean {
  for (let colName in row) {
    if (!Number.isInteger(row[colName]) && !isBoolean(row[colName]) && !(typeof row[colName] === "object")) {
      if(isString(row[colName]) && toLower(row[colName]).indexOf(keyword) != -1) {
        return true
      } else if (row[colName].indexOf(keyword) != -1) {
        // field value contains keyword
        return true
      }
    } else if (isArray(row[colName]) && isString(row[colName][0])) {
      for (let i=0; i<row[colName].length; i++) {
        if(toLower(row[colName][i]).indexOf(keyword) != -1) {
          return true
        }
      }
    }
  }
  return false
}
