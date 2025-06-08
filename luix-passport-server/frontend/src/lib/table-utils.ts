import { ColumnSort } from "@tanstack/react-table"
import { isBoolean, isString, isArray, cloneDeep, toLower } from "lodash"

export function parseSorts(sorting: Array<ColumnSort>): Array<string> | undefined {
  return sorting?.length
    ? sorting.map(({ id, desc }) => `${id},${desc ? "desc" : "asc"}`)
    : undefined
}

export function filterTable<T extends Record<string, unknown>>(
  initialTableData: T[],
  searchKeyword: string
): T[] {
  const tableData = cloneDeep(initialTableData);
  const results: T[] = [];

  for (let i = 0; i < tableData.length; i++) {
    if (searchRow(tableData[i], searchKeyword)) {
      results.push(tableData[i]);
    }
  }

  return cloneDeep(results);
}

function searchRow(row: Record<string, unknown>, keyword: string): boolean {
  for (const colName in row) {
    if (!Number.isInteger(row[colName]) && !isBoolean(row[colName]) && !(typeof row[colName] === "object")) {
      if(isString(row[colName]) && toLower(row[colName]).indexOf(keyword) != -1) {
        return true
      } else if (row[colName] && String(row[colName]).indexOf(keyword) != -1) {
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
