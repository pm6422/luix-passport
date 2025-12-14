import { useState, useEffect, ReactNode } from "react"
import { zodResolver } from "@hookform/resolvers/zod"
import { useForm, FieldValues, UseFormReturn, DefaultValues } from "react-hook-form"
import { Dialog, DialogTrigger } from "@/components/ui/dialog"
import SaveDialogContent from "@/components/custom/dialog/save-dialog-content"
import type { z } from "zod"

interface LoadResult<TOptions = any> {
  options?: TOptions
  entity?: any
}

interface SaveDialogProps<
  TFieldValues extends FieldValues,
  TOptions = any
> {
  /**
   * Trigger element for the dialog
   */
  children: ReactNode

  /**
   * Entity name for display (e.g., "role", "user")
   */
  entityName: string

  /**
   * Entity ID for edit mode, null/undefined for create mode
   */
  id?: string | null

  /**
   * Zod schema for form validation
   */
  schema: z.ZodType<TFieldValues, z.ZodTypeDef, any>

  /**
   * Default values for the form
   */
  defaultValues: DefaultValues<TFieldValues>

  /**
   * Save function that receives form data
   */
  save: (formData: TFieldValues) => Promise<void>

  /**
   * Callback after save completes
   */
  afterSave?: (success: boolean) => void

  /**
   * Data loading function called when dialog opens
   * Should return options and entity data
   */
  onLoad?: (id?: string | null) => Promise<LoadResult<TOptions>>

  /**
   * Render function for form fields
   * Receives form instance and loaded options
   */
  renderFields: (form: UseFormReturn<TFieldValues>, options: TOptions) => ReactNode

  /**
   * Dialog size
   */
  size?: "sm" | "md" | "lg"

  /**
   * Readonly mode (view only)
   */
  readonly?: boolean

  /**
   * Debug mode (shows form values)
   */
  debug?: boolean
}

export function SaveDialog<
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
}: SaveDialogProps<TFieldValues, TOptions>) {
  const [open, setOpen] = useState(false)
  const [options, setOptions] = useState<TOptions>({} as TOptions)
  const [loading, setLoading] = useState(false)

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
        if (onLoad) {
          const result = await onLoad(id)

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
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [open, id, onLoad])

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
