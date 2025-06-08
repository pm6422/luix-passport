import { z } from "zod"
import { BaseCriteria } from "./base/base-criteria"

export const dataDictSchema = z.object({
  id: z.string().optional().nullable(),
  num: z.string().optional().nullable(),
  categoryCode: z.string().trim().min(1, { message: "Required" }),
  dictCode: z.string().trim().min(1, { message: "Required" }),
  dictName: z.string().optional().nullable(),
  remark: z.string().optional().nullable(),
  enabled: z.boolean().optional().nullable(),
  modifiedAt: z.string().optional().nullable()
})

export type DataDict = z.infer<typeof dataDictSchema>

export const initialDataDictState: DataDict = {
  id: "",
  num: "",
  categoryCode: "",
  dictCode: "",
  dictName: "",
  remark: "",
  enabled: true,
  modifiedAt: ""
}

export const dataDictCriteriaSchema = z.object({
  num: z.string().optional().nullable(),
  categoryCode: z.string().optional().nullable(),
  enabled: z.string().optional().nullable()
})

export type DataDictCriteria = z.infer<typeof dataDictCriteriaSchema> & BaseCriteria

export const initialDataDictCriteriaState: DataDictCriteria = {
  num: null,
  categoryCode: null,
  enabled: null
}