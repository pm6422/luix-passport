import { type ClassValue, clsx } from "clsx"
import { twMerge } from "tailwind-merge"
import dayjs from "dayjs"
import { customAlphabet } from "nanoid"
import { ColumnSort } from "@tanstack/react-table"
import { isBoolean, isString, isArray, cloneDeep, toLower } from "lodash"

export function cn(...inputs: ClassValue[]) {
  return twMerge(clsx(inputs))
}

export function formatDateTime(dateTimeFormat: string, value: string | Date, timeZone: string): string {
  if (!value) {
    return ""
  }
  return dayjs(value).tz(timeZone).format(dateTimeFormat)
}

export function formatDate(dateFormat: string, value: string | Date, timeZone: string): string {
  if (!value) {
    return ""
  }
  return dayjs(value).tz(timeZone).format(dateFormat)
}

export function generateId({ length = 8, prefix = "" } = {}) {
  return `${prefix}${customAlphabet(
    "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz",
    length
  )()}`
}

export function parseSorts(sorting: Array<ColumnSort>): Array<string> | undefined {
  return sorting?.length
    ? sorting.map(({ id, desc }) => `${id},${desc ? "desc" : "asc"}`)
    : undefined
}

export function merge(source: any, target: any): any {
  return Object.entries(source).reduce((acc, [key, value]) => {
    if (value !== null && value !== undefined) {
      acc[key] = value
    }
    return acc
  }, { ...target })
}

export function toBase64(file: File) {
	return new Promise((resolve, reject) => {
		const fileReader = new FileReader()
		
		fileReader.readAsDataURL(file)
		
		fileReader.onload = () => {
			resolve(fileReader.result)
		}
		
		fileReader.onerror = (error) => {
			reject(error)
		}
	})
}

export function fromBase64(base64String: string, fileName: string) {
  // Remove metadata prefix from base64 string
  const base64WithoutMetadata = base64String.replace(/^data:\w+\/\w+;base64,/, "")

  // Convert Base64 to Blob
  const byteCharacters = atob(base64WithoutMetadata)
  const byteArrays = []
  for (let offset = 0; offset < byteCharacters.length; offset += 512) {
    const slice = byteCharacters.slice(offset, offset + 512)
    const byteNumbers = new Array(slice.length)
    for (let i = 0; i < slice.length; i++) {
      byteNumbers[i] = slice.charCodeAt(i)
    }
    const byteArray = new Uint8Array(byteNumbers)
    byteArrays.push(byteArray)
  }
  const blob = new Blob(byteArrays, { type: "application/octet-stream" })

  // Convert Blob to File
  return new File([blob], fileName, { type: blob.type })
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
