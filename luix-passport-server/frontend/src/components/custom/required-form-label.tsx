import { FormLabel } from "@/components/ui/form"

type Props = {
  children: string
  required?: boolean
  htmlFor?: string
}

export const RequiredFormLabel = ({
  children,
  required = false,
  htmlFor
}: Props) => (
  <FormLabel htmlFor={htmlFor} className="flex items-center gap-1">
    {children}
    {required && <span className="text-destructive font-bold">*</span>}
  </FormLabel>
)