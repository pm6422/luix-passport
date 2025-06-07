import { LayoutBody } from "@/layouts/layout-definitions"
import { Card } from "@/components/ui/card"

export default function DbSchema() {
  return (
    <LayoutBody className="flex flex-col" fixedHeight>
      <Card>
        <iframe
          src="https://dbdocs.io/pm6422/luix-passport"
          className="rounded-lg"
          width="100%"
          height="900">
        </iframe>
      </Card>
    </LayoutBody>
  )
}
