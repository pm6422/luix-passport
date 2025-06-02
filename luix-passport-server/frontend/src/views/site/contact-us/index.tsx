import { useState } from "react"
import { useForm } from "react-hook-form"
import { zodResolver } from "@hookform/resolvers/zod"
import { z } from "zod"
import { toast } from "sonner"
import { LoadingButton } from "@/components/custom/loading-button"
import { getErrorMessage } from "@/lib/handle-error"
import { ContactService } from "@/services/contact-service"
import InputFormField from "@/components/custom/form-field/input"
import SelectFormField from "@/components/custom/form-field/select"
import { Form } from "@/components/ui/form"
import { useNavigate } from "react-router-dom"


// Define the form schema with updated field names
const contactFormSchema = z.object({
  sender: z.string().trim().min(1, { message: "Required" }),
  senderEmail: z.string().email("Please enter a valid email address"),
  title: z.string().trim().min(1, { message: "Required" }),
  content: z.string().trim().min(1, { message: "Required" }),
})

export type ContactFormSchema = z.infer<typeof contactFormSchema>

export default function ContactUs() {
  const [isLoading, setIsLoading] = useState(false)
  const navigate = useNavigate()
  const subjects = [
    {
      label: "Technical Support",
      value: "Technical Support",
    },
    {
      label: "Billing Inquiry",
      value: "Billing Inquiry",
    },
    {
      label: "Product Feedback",
      value: "Product Feedback",
    },
    {
      label: "Other Problem",
      value: "Other Problem",
    },
  ]

  const form = useForm<ContactFormSchema>({
    resolver: zodResolver(contactFormSchema),
    defaultValues: {
      sender: "",
      senderEmail: "",
      title: "",
      content: "",
    },
  })

  async function onSubmit(data: ContactFormSchema) {
    setIsLoading(true)

    toast.promise(ContactService.create(data), {
      loading: "Sending message...",
      success: () => {
        setIsLoading(false)
        navigate("/submitted")
        return "Sent message successfully"
      },
      error: (error) => {
        setIsLoading(false)
        return getErrorMessage(error)
      },
    })
  }

  return (
    <div className="container relative grid flex-col items-center justify-center lg:max-w-none lg:grid-cols-2">
      {/* Left side - hidden on mobile */}
      <div className="relative mb-36 ms-10 hidden lg:flex lg:px-28">
        <div className="max-md:text-center">
          <img
            src="/assets/images/logos/logo-with-text.svg"
            alt="Logo"
            className="my-12 h-14"
          />
          <h2 className="text-4xl font-extrabold lg:text-5xl lg:leading-[55px]">
            We are Here to Help
          </h2>
          <h4 className="mt-6 text-lg">
            Have questions or feedback? Reach out to our team anytime.
          </h4>
          <p className="mt-10 text-sm">Typically respond within 24 hours</p>
        </div>
      </div>

      {/* Right side - form */}
      <div className="flex w-full flex-col rounded-3xl px-5 py-10 lg:px-28 xl:px-48">
        <img
          src="/assets/images/logos/logo-round.svg"
          alt="Logo"
          className="mx-auto my-5 h-16"
        />

        <div className="mx-auto w-full max-w-md">
          <h1 className="mb-2 text-center text-2xl font-bold">Contact Us</h1>
          <p className="mb-8 text-center text-sm text-muted-foreground">
            Fill out the form below and we"ll get back to you soon
          </p>

          <Form {...form}>
            <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-6">
              <InputFormField
                control={form.control}
                name="sender"
                label="Full Name"
                formItemClassName="text-left"
                inputClassName="w-full h-14 px-5 py-4 rounded-2xl"
                placeholder="Your name"
                required={true}
              />

              <InputFormField
                control={form.control}
                name="senderEmail"
                label="Email Address"
                formItemClassName="text-left"
                inputClassName="w-full h-14 px-5 py-4 rounded-2xl"
                placeholder="your@email.com"
                required={true}
              />

              <SelectFormField
                control={form.control}
                name="title"
                label="Subject"
                options={subjects}
                inputClassName="w-full h-14 px-5 py-4 rounded-2xl"
                formItemClassName="w-full"
                placeholder="Select a subject"
                required
              />

              <InputFormField
                control={form.control}
                name="content"
                label="Message"
                type="textarea"
                formItemClassName="text-left"
                inputClassName="w-full h-40 px-5 py-4 rounded-2xl"
                placeholder="How can we help you?"
                required={true}
              />

              <LoadingButton
                type="submit"
                loading={isLoading}
                className="mt-4 h-14 w-full rounded-2xl bg-primary px-5 py-4 text-base font-medium text-primary-foreground hover:bg-primary/90"
              >
                {isLoading ? "Sending..." : "Send Message"}
              </LoadingButton>
            </form>
          </Form>

          <div className="mt-8 border-t pt-8">
            <h3 className="mb-4 text-lg font-semibold">
              Other Ways to Reach Us
            </h3>
            <div className="space-y-3">
              <div className="flex items-center">
                <svg
                  className="mr-3 h-5 w-5"
                  fill="none"
                  viewBox="0 0 24 24"
                  stroke="currentColor"
                >
                  <path
                    strokeLinecap="round"
                    strokeLinejoin="round"
                    strokeWidth={2}
                    d="M3 8l7.89 5.26a2 2 0 002.22 0L21 8M5 19h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v10a2 2 0 002 2z"
                  />
                </svg>
                <span>helpdesk@luixtech.cn</span>
              </div>
              <div className="flex items-center">
                <svg
                  className="mr-3 h-5 w-5"
                  fill="none"
                  viewBox="0 0 24 24"
                  stroke="currentColor"
                >
                  <path
                    strokeLinecap="round"
                    strokeLinejoin="round"
                    strokeWidth={2}
                    d="M3 5a2 2 0 012-2h3.28a1 1 0 01.948.684l1.498 4.493a1 1 0 01-.502 1.21l-2.257 1.13a11.042 11.042 0 005.516 5.516l1.13-2.257a1 1 0 011.21-.502l4.493 1.498a1 1 0 01.684.949V19a2 2 0 01-2 2h-1C9.716 21 3 14.284 3 6V5z"
                  />
                </svg>
                <span>+1 (555) 123-4567</span>
              </div>
              <div className="flex items-center">
                <svg
                  className="mr-3 h-5 w-5"
                  fill="none"
                  viewBox="0 0 24 24"
                  stroke="currentColor"
                >
                  <path
                    strokeLinecap="round"
                    strokeLinejoin="round"
                    strokeWidth={2}
                    d="M17.657 16.657L13.414 20.9a1.998 1.998 0 01-2.827 0l-4.244-4.243a8 8 0 1111.314 0z"
                  />
                  <path
                    strokeLinecap="round"
                    strokeLinejoin="round"
                    strokeWidth={2}
                    d="M15 11a3 3 0 11-6 0 3 3 0 016 0z"
                  />
                </svg>
                <span>
                  123 Business Ave, Suite 100
                  <br />
                  San Francisco, CA 94107
                </span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  )
}
