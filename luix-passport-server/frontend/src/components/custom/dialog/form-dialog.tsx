import { useState, useEffect, ReactNode, useRef } from "react"
import { zodResolver } from "@hookform/resolvers/zod"
import { useForm, FieldValues, UseFormReturn, DefaultValues } from "react-hook-form"
import { Dialog, DialogTrigger } from "@/components/ui/dialog"
import SaveDialogContent from "@/components/custom/dialog/save-dialog-content"
import type { z } from "zod"

interface LoadResult<TOptions = any> {
  options?: TOptions
  entity?: any
}

interface FormDialogProps<
  TFieldValues extends FieldValues,
  TOptions = any
> {
  children: ReactNode
  entityName: string
  id?: string | null
  schema: z.ZodType<TFieldValues, z.ZodTypeDef, any>
  defaultValues: DefaultValues<TFieldValues>
  save: (formData: TFieldValues) => Promise<void>
  afterSave?: (success: boolean) => void
  onLoad?: (id?: string | null) => Promise<LoadResult<TOptions>>
  renderFields: (form: UseFormReturn<TFieldValues>, options: TOptions) => ReactNode
  size?: "sm" | "md" | "lg"
  readonly?: boolean
  debug?: boolean
}

export function FormDialog<
  TFieldValues extends FieldValues,
  TOptions = any
>({
  children,
  entityName,
  id,
  schema,
  defaultValues,
  save,
  afterSave,
  onLoad,
  renderFields,
  size = "md",
  readonly = false,
  debug = false,
}: FormDialogProps<TFieldValues, TOptions>) {
  const [open, setOpen] = useState(false)
  const [options, setOptions] = useState<TOptions>({} as TOptions)
  const [loading, setLoading] = useState(false)
  const onLoadRef = useRef(onLoad)

  useEffect(() => {
    onLoadRef.current = onLoad
  }, [onLoad])

  const form = useForm<TFieldValues>({
    // @ts-expect-error - zodResolver has complex type constraints that are difficult to satisfy with generic schemas
    resolver: zodResolver(schema),
    defaultValues,
  })

  useEffect(() => {
    if (!open) {
      return
    }

    const loadData = async () => {
      setLoading(true)
      try {
        const loader = onLoadRef.current
        if (loader) {
          const result = await loader(id)

          if (result.options) {
            setOptions(result.options)
          }

          if (result.entity) {
            form.reset(result.entity)
          }
        }
      } catch (error) {
        console.error("Failed to load data:", error)
      } finally {
        setLoading(false)
      }
    }

    loadData()
  }, [open, id, form])

  return (
    <Dialog open={open} onOpenChange={setOpen}>
      <DialogTrigger asChild>
        {children}
      </DialogTrigger>
      <SaveDialogContent<TFieldValues>
        entityName={entityName}
        id={id}
        form={form}
        save={save}
        afterSave={afterSave}
        setOpen={setOpen}
        size={size}
        readonly={readonly}
        debug={debug}
      >
        {loading ? (
          <div className="flex items-center justify-center py-8">
            <div className="text-sm text-muted-foreground">Loading...</div>
          </div>
        ) : (
          renderFields(form, options)
        )}
      </SaveDialogContent>
    </Dialog>
  )
}
