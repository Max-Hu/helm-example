{{- define "mylib-chart.deployment" -}}
apiVersion: apps/v1
kind: Deployment
metadata:
  name: {{ include "mylib-chart.fullname" . }}
  labels:
    {{- include "mylib-chart.labels" . | nindent 4 }}
spec:
  replicas: {{ .Values.replicas }}
  selector:
    matchLabels:
      app: {{ include "mylib-chart.name" . }}
  template:
    metadata:
      labels:
        app: {{ include "mylib-chart.name" . }}
    spec:
      containers:
        - name: {{ .Chart.Name }}
          image: "{{ .Values.image.repository }}:{{ .Values.image.tag }}"
          ports:
            - containerPort: {{ .Values.containerPort }}
{{- end }}
