import { UseFormReturn } from "react-hook-form"
import { GeneratePromoCode } from "@/domains/promo-code"
import InputFormField from "@/components/custom/form-field/input"
import SelectFormField from "@/components/custom/form-field/select"

interface GeneratePromoCodeFormFieldsProps {
  form: UseFormReturn<GeneratePromoCode>
  roleOptions: { label: string, value: string }[]
}

export function GeneratePromoCodeFormFields({
  form,
  roleOptions
}: GeneratePromoCodeFormFieldsProps) {
  return (
    <div className="grid gap-4 py-4">
      <InputFormField
        control={form.control}
        name="count"
        label="Count"
        placeholder="Number of codes to generate"
        type="number"
      />
      <SelectFormField
        control={form.control}
        name="type"
        label="Membership Type"
        placeholder="Select membership type"
        options={roleOptions}
      />
      <InputFormField
        control={form.control}
        name="durationDays"
        label="Duration (Days)"
        placeholder="Duration in days"
        type="number"
      />
      <InputFormField
        control={form.control}
        name="batchId"
        label="Batch ID (Optional)"
        placeholder="Custom batch ID"
      />
      <InputFormField
        control={form.control}
        name="expiry"
        label="Expiry Date (Optional)"
        placeholder="YYYY-MM-DD"
        type="date"
      />
    </div>
  )
}
