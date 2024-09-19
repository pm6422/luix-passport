import { z } from "zod"

export const dataDictSchema = z.object({
  id: z.string().optional(),
  num: z.string().optional(),
  categoryCode: z.string().trim().min(1, { message: "Required" }),
  dictCode: z.string().trim().min(1, { message: "Required" }),
  dictName: z.string().optional(),
  remark: z.string().optional(),
  enabled: z.boolean().optional(),
  modifiedAt: z.string().optional()
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
  num: z.string().optional(),
  categoryCode: z.string().optional(),
  enabled: z.string().optional()
})

export type DataDictCriteria = z.infer<typeof dataDictCriteriaSchema>

export const initialDataDictCriteriaState: DataDictCriteria = {
  num: "",
  categoryCode: "",
  enabled: ""
}