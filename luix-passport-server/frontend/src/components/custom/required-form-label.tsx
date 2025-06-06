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
  <FormLabel htmlFor={htmlFor}>
    {children}
    {required && <span className="ml-1 text-destructive font-bold align-middle">*</span>}
  </FormLabel>
)